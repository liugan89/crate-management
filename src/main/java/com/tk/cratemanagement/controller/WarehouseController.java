package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.service.WarehouseService;
import com.tk.cratemanagement.util.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓储操作控制器
 * 处理核心仓储业务的REST API端点：入库、出库、调整等操作
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "仓储操作", description = "出入库单据和扫码操作相关API")
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 创建出入库单据
     */
    @PostMapping("/shipment-orders")
    @Operation(summary = "创建出入库单据", description = "创建新的入库、出库或调整单据")
    public ResponseEntity<ShipmentOrderDTO> createShipmentOrder(
            @Valid @RequestBody CreateShipmentOrderRequestDTO request,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到创建单据请求: type={}, tenantId={}", request.type(), tenantId);
        
        ShipmentOrderDTO response = warehouseService.createShipmentOrder(request, tenantId);
        
        log.info("单据创建成功: orderId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取单据列表
     */
    @GetMapping("/shipment-orders")
    @Operation(summary = "获取单据列表", description = "获取出入库单据列表，支持按类型和状态过滤")
    public ResponseEntity<List<ShipmentOrderSummaryDTO>> getShipmentOrders(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("获取单据列表: tenantId={}, type={}, status={}", tenantId, type, status);
        
        List<ShipmentOrderSummaryDTO> orders = warehouseService.getShipmentOrders(tenantId, type, status);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * 获取单据详情
     */
    @GetMapping("/shipment-orders/{id}")
    @Operation(summary = "获取单据详情", description = "获取单据的完整详情，包括行项和扫码记录")
    public ResponseEntity<ShipmentOrderDetailsDTO> getShipmentOrderDetails(
            @PathVariable Long id,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("获取单据详情: orderId={}, tenantId={}", id, tenantId);
        
        ShipmentOrderDetailsDTO details = warehouseService.getShipmentOrderDetails(id, tenantId);
        
        return ResponseEntity.ok(details);
    }

    /**
     * 完成单据
     * 核心业务逻辑端点：原子性操作，触发库存更新
     */
    @PostMapping("/shipment-orders/{id}/complete")
    @Operation(summary = "完成单据", description = "完成出入库单据，触发库存更新（原子性操作）")
    public ResponseEntity<ShipmentOrderDetailsDTO> completeShipmentOrder(
            @PathVariable Long id,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到完成单据请求: orderId={}, tenantId={}", id, tenantId);
        
        ShipmentOrderDetailsDTO response = warehouseService.completeShipmentOrder(id, tenantId);
        
        log.info("单据完成成功: orderId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 为单据添加行项
     */
    @PostMapping("/shipment-orders/{orderId}/items")
    @Operation(summary = "添加单据行项", description = "为待处理的单据添加货物行项")
    public ResponseEntity<OrderItemDTO> addOrderItem(
            @PathVariable Long orderId,
            @Valid @RequestBody CreateOrderItemRequestDTO request,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到添加行项请求: orderId={}, goodsId={}, tenantId={}", 
                orderId, request.goodsId(), tenantId);
        
        OrderItemDTO response = warehouseService.addOrderItem(orderId, request, tenantId);
        
        log.info("行项添加成功: itemId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 为行项添加扫码记录
     * 高频操作：移动端NFC扫描
     */
    @PostMapping("/shipment-order-items/{itemId}/scans")
    @Operation(summary = "添加扫码记录", description = "为单据行项添加NFC扫码记录（高频操作）")
    public ResponseEntity<ScanDTO> addScan(
            @PathVariable Long itemId,
            @Valid @RequestBody CreateScanRequestDTO request,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        Long userId = AuthUtils.getUserIdFromAuth(authentication);
        log.info("收到扫码请求: itemId={}, nfcUid={}, tenantId={}, userId={}", 
                itemId, request.nfcUid(), tenantId, userId);
        
        ScanDTO response = warehouseService.addScan(itemId, request, tenantId, userId);
        
        log.info("扫码记录添加成功: scanId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}