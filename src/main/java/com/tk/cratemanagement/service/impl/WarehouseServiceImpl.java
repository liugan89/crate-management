package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.*;
import com.tk.cratemanagement.domain.enumeration.*;
import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.repository.*;
import com.tk.cratemanagement.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 仓储操作服务实现类
 * 实现核心仓储业务逻辑，包括入库、出库、调整等原子性操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final ShipmentOrderRepository shipmentOrderRepository;
    private final ShipmentOrderItemRepository shipmentOrderItemRepository;
    private final ShipmentOrderItemScanRepository scanRepository;
    private final ObjectMapper objectMapper;
    private final CrateRepository crateRepository;
    private final CrateContentRepository crateContentRepository;
    private final GoodsRepository goodsRepository;
    private final SupplierRepository supplierRepository;
    private final OperationLogRepository operationLogRepository;

    @Override
    @Transactional
    public ShipmentOrderDTO createShipmentOrder(CreateShipmentOrderRequestDTO request, Long tenantId) {
        log.info("创建出入库单据: type={}, tenantId={}", request.type(), tenantId);

        ShipmentOrder order = new ShipmentOrder();
        order.setTenantId(tenantId);
        order.setOrderNumber(generateOrderNumber());
        order.setType(request.type());
        order.setStatus(ShipmentOrderStatus.PENDING);
        order.setNotes(request.notes());
        order.setCreatedAt(Instant.now());

        // 如果是调整单，设置原始单据关联
        if (request.originalOrderId() != null) {
            ShipmentOrder originalOrder = shipmentOrderRepository.findByIdAndTenantId(request.originalOrderId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("原始单据不存在"));
            order.setOriginalOrder(originalOrder);
        }

        order = shipmentOrderRepository.save(order);
        log.info("单据创建成功: orderId={}, orderNumber={}", order.getId(), order.getOrderNumber());

        return convertToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentOrderSummaryDTO> getShipmentOrders(Long tenantId, String type, String status) {
        log.debug("获取单据列表: tenantId={}, type={}, status={}", tenantId, type, status);

        List<ShipmentOrder> orders;
        if (type != null && status != null) {
            orders = shipmentOrderRepository.findByTenantIdAndTypeAndStatus(
                    tenantId, ShipmentOrderType.valueOf(type), ShipmentOrderStatus.valueOf(status));
        } else if (type != null) {
            orders = shipmentOrderRepository.findByTenantIdAndType(tenantId, ShipmentOrderType.valueOf(type));
        } else if (status != null) {
            orders = shipmentOrderRepository.findByTenantIdAndStatus(tenantId, ShipmentOrderStatus.valueOf(status));
        } else {
            orders = shipmentOrderRepository.findByTenantId(tenantId);
        }

        return orders.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentOrderDetailsDTO getShipmentOrderDetails(Long orderId, Long tenantId) {
        log.debug("获取单据详情: orderId={}, tenantId={}", orderId, tenantId);

        ShipmentOrder order = shipmentOrderRepository.findByIdAndTenantId(orderId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("单据不存在"));

        return convertToDetailsDTO(order);
    }

    @Override
    @Transactional
    public ShipmentOrderDetailsDTO completeShipmentOrder(Long orderId, Long tenantId) {
        log.info("开始完成单据: orderId={}, tenantId={}", orderId, tenantId);

        ShipmentOrder order = shipmentOrderRepository.findByIdAndTenantId(orderId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("单据不存在"));

        if (order.getStatus() != ShipmentOrderStatus.PENDING && order.getStatus() != ShipmentOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("只能完成待处理或进行中的单据");
        }

        // 获取所有扫码记录
        List<ShipmentOrderItemScan> allScans = scanRepository.findByOrderItemShipmentOrderId(orderId);
        
        if (allScans.isEmpty()) {
            throw new IllegalStateException("单据没有扫码记录，无法完成");
        }

        // 核心业务逻辑：处理每个扫码记录
        for (ShipmentOrderItemScan scan : allScans) {
            processShipmentScan(scan, order.getType(), tenantId);
        }

        // 更新单据状态
        order.setStatus(ShipmentOrderStatus.COMPLETED);
        order = shipmentOrderRepository.save(order);

        log.info("单据完成成功: orderId={}", orderId);
        return convertToDetailsDTO(order);
    }

    @Override
    @Transactional
    public OrderItemDTO addOrderItem(Long orderId, CreateOrderItemRequestDTO request, Long tenantId) {
        log.info("添加单据行项: orderId={}, goodsId={}", orderId, request.goodsId());

        ShipmentOrder order = shipmentOrderRepository.findByIdAndTenantId(orderId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("单据不存在"));

        if (order.getStatus() != ShipmentOrderStatus.PENDING) {
            throw new IllegalStateException("只能为待处理状态的单据添加行项");
        }

        // 验证货物和供应商
        Goods goods = goodsRepository.findByIdAndTenantId(request.goodsId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("货物不存在"));

        Supplier supplier = null;
        if (request.supplierId() != null) {
            supplier = supplierRepository.findByIdAndTenantId(request.supplierId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));
        }

        ShipmentOrderItem item = new ShipmentOrderItem();
        item.setShipmentOrder(order);
        item.setGoods(goods);
        item.setSupplier(supplier);
        item.setExpectedQuantity(request.expectedQuantity() != null ? BigDecimal.valueOf(request.expectedQuantity()) : null);
        item.setBatchNumber(request.batchNumber());
        item.setProductionDate(request.productionDate());

        item = shipmentOrderItemRepository.save(item);
        log.info("行项添加成功: itemId={}", item.getId());

        return convertToOrderItemDTO(item);
    }

    @Override
    @Transactional
    public ScanDTO addScan(Long itemId, CreateScanRequestDTO request, Long tenantId, Long userId) {
        log.info("添加扫码记录: itemId={}, nfcUid={}", itemId, request.nfcUid());

        ShipmentOrderItem item = shipmentOrderItemRepository.findByIdAndShipmentOrderTenantId(itemId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("单据行项不存在"));

        if (item.getShipmentOrder().getStatus() == ShipmentOrderStatus.COMPLETED) {
            throw new IllegalStateException("已完成的单据不能添加扫码记录");
        }

        // 验证周转筐
        Crate crate = crateRepository.findByTenantIdAndNfcUid(tenantId, request.nfcUid())
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在: " + request.nfcUid()));

        // 检查是否已经扫码
        if (scanRepository.findByOrderItemIdAndCrateId(itemId, crate.getId()).isPresent()) {
            throw new IllegalArgumentException("该周转筐已经扫码");
        }

        ShipmentOrderItemScan scan = new ShipmentOrderItemScan();
        scan.setOrderItem(item);
        scan.setCrate(crate);
        scan.setActualQuantity(BigDecimal.valueOf(request.actualQuantity()));
        scan.setScannedAt(Instant.now());
        scan.setScannedByUserId(userId);

        scan = scanRepository.save(scan);

        // 更新单据状态为进行中
        if (item.getShipmentOrder().getStatus() == ShipmentOrderStatus.PENDING) {
            item.getShipmentOrder().setStatus(ShipmentOrderStatus.IN_PROGRESS);
            shipmentOrderRepository.save(item.getShipmentOrder());
        }

        log.info("扫码记录添加成功: scanId={}", scan.getId());
        return convertToScanDTO(scan);
    }

    /**
     * 处理单个扫码记录的库存更新
     * 这是核心业务逻辑，必须在事务中执行
     */
    private void processShipmentScan(ShipmentOrderItemScan scan, ShipmentOrderType orderType, Long tenantId) {
        Crate crate = scan.getCrate();
        ShipmentOrderItem item = scan.getOrderItem();

        log.debug("处理扫码记录: scanId={}, crateId={}, orderType={}", scan.getId(), crate.getId(), orderType);

        // 获取或创建周转筐内容记录
        CrateContent content = crateContentRepository.findByCrateId(crate.getId())
                .orElse(new CrateContent());
        
        // 如果是新创建的内容记录，设置crate
        if (content.getId() == null) {
            content.setCrate(crate);
        }

        switch (orderType) {
            case INBOUND -> processInboundScan(content, crate, item, scan, tenantId);
            case OUTBOUND -> processOutboundScan(content, crate, item, scan, tenantId);
            case INBOUND_ADJUSTMENT -> processInboundAdjustmentScan(content, crate, item, scan, tenantId);
            case OUTBOUND_ADJUSTMENT -> processOutboundAdjustmentScan(content, crate, item, scan, tenantId);
        }

        // 记录操作日志
        Map<String, Object> scanInfo = Map.of(
            "scanId", scan.getId(),
            "crateId", crate.getId(),
            "crateNfcUid", crate.getNfcUid(),
            "actualQuantity", scan.getActualQuantity(),
            "scannedAt", scan.getScannedAt()
        );
        recordOperationLog(tenantId, scan.getScannedByUserId(), "SHIPMENT_SCAN", 
                scan.getId(), orderType.name(), scanInfo);
    }

    private void processInboundScan(CrateContent content, Crate crate, ShipmentOrderItem item, 
                                  ShipmentOrderItemScan scan, Long tenantId) {
        // 入库：更新周转筐状态和内容
        crate.setStatus(CrateStatus.IN_USE);
        crateRepository.save(crate);

        content.setCrate(crate);
        content.setTenantId(tenantId);
        content.setGoods(item.getGoods());
        content.setSupplier(item.getSupplier());
        content.setBatchNumber(item.getBatchNumber());
        content.setQuantity(scan.getActualQuantity());
        content.setStatus(CrateContentStatus.INBOUND);
        content.setLastUpdatedAt(Instant.now());
        content.setLastUpdatedByOrderId(item.getShipmentOrder().getId());

        crateContentRepository.save(content);
        log.debug("入库处理完成: crateId={}, quantity={}", crate.getId(), scan.getActualQuantity());
    }

    private void processOutboundScan(CrateContent content, Crate crate, ShipmentOrderItem item, 
                                   ShipmentOrderItemScan scan, Long tenantId) {
        // 出库：验证当前状态并更新
        if (content.getId() == null || content.getStatus() != CrateContentStatus.INBOUND) {
            throw new IllegalStateException("周转筐当前不是入库状态，无法出库: " + crate.getNfcUid());
        }

        content.setStatus(CrateContentStatus.OUTBOUND);
        content.setLastUpdatedAt(Instant.now());
        content.setLastUpdatedByOrderId(item.getShipmentOrder().getId());

        crateContentRepository.save(content);
        crate.setStatus(CrateStatus.OUTBOUND);
        crateRepository.save(crate);

        log.debug("出库处理完成: crateId={}", crate.getId());
    }

    private void processInboundAdjustmentScan(CrateContent content, Crate crate, ShipmentOrderItem item, 
                                            ShipmentOrderItemScan scan, Long tenantId) {
        // 入库调整：撤销入库操作
        if (content.getId() == null || content.getStatus() != CrateContentStatus.INBOUND) {
            throw new IllegalStateException("周转筐当前不是入库状态，无法进行入库调整: " + crate.getNfcUid());
        }

        // 清空内容，恢复可用状态
        crateContentRepository.delete(content);
        crate.setStatus(CrateStatus.AVAILABLE);
        crateRepository.save(crate);

        log.debug("入库调整处理完成: crateId={}", crate.getId());
    }

    private void processOutboundAdjustmentScan(CrateContent content, Crate crate, ShipmentOrderItem item, 
                                             ShipmentOrderItemScan scan, Long tenantId) {
        // 出库调整：撤销出库操作
        if (content.getId() == null || content.getStatus() != CrateContentStatus.OUTBOUND) {
            throw new IllegalStateException("周转筐当前不是出库状态，无法进行出库调整: " + crate.getNfcUid());
        }

        content.setStatus(CrateContentStatus.INBOUND);
        content.setLastUpdatedAt(Instant.now());
        content.setLastUpdatedByOrderId(item.getShipmentOrder().getId());

        crateContentRepository.save(content);
        crate.setStatus(CrateStatus.IN_USE);
        crateRepository.save(crate);

        log.debug("出库调整处理完成: crateId={}", crate.getId());
    }

    private void recordOperationLog(Long tenantId, Long userId, String operationType, 
                                  Long entityId, String description, Object payload) {
        OperationLog operationLog = new OperationLog();
        operationLog.setTenantId(tenantId);
        operationLog.setUserId(userId);
        operationLog.setEntityType("SHIPMENT_SCAN");
        operationLog.setEntityId(entityId);
        operationLog.setOperationType(operationType);
        
        // 将payload对象转换为JSON字符串
        String payloadJson = null;
        if (payload != null) {
            try {
                payloadJson = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                log.warn("Failed to serialize payload to JSON: {}", e.getMessage());
                payloadJson = payload.toString();
            }
        }
        operationLog.setPayload(payloadJson);
        operationLog.setCreatedAt(Instant.now());

        operationLogRepository.save(operationLog);
    }

    private String generateOrderNumber() {
        return "SO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    // DTO转换方法
    private ShipmentOrderDTO convertToDTO(ShipmentOrder order) {
        return new ShipmentOrderDTO(
                order.getId(),
                order.getType(),
                order.getStatus(),
                order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()),
                null, // completedAt
                order.getNotes()
        );
    }

    private ShipmentOrderSummaryDTO convertToSummaryDTO(ShipmentOrder order) {
        return new ShipmentOrderSummaryDTO(
                order.getId(),
                order.getType(),
                order.getStatus(),
                order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()),
                order.getItems() != null ? order.getItems().size() : 0
        );
    }

    private ShipmentOrderDetailsDTO convertToDetailsDTO(ShipmentOrder order) {
        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());

        return new ShipmentOrderDetailsDTO(
                order.getId(),
                order.getType(),
                order.getStatus(),
                order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()),
                null, // completedAt
                order.getNotes(),
                items
        );
    }

    private OrderItemDTO convertToOrderItemDTO(ShipmentOrderItem item) {
        List<ScanDTO> scans = item.getScans().stream()
                .map(this::convertToScanDTO)
                .collect(Collectors.toList());

        return new OrderItemDTO(
                item.getId(),
                item.getGoods().getId(),
                item.getGoods().getName(),
                item.getSupplier() != null ? item.getSupplier().getId() : null,
                item.getSupplier() != null ? item.getSupplier().getName() : null,
                item.getBatchNumber(),
                item.getProductionDate(),
                item.getExpectedQuantity() != null ? item.getExpectedQuantity().doubleValue() : 0.0,
                scans
        );
    }

    private ScanDTO convertToScanDTO(ShipmentOrderItemScan scan) {
        return new ScanDTO(
                scan.getId(),
                scan.getCrate().getNfcUid(),
                scan.getScannedAt().atZone(java.time.ZoneId.systemDefault()),
                scan.getActualQuantity().doubleValue()
        );
    }
}