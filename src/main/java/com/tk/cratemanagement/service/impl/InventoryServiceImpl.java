package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.Crate;
import com.tk.cratemanagement.domain.CrateContent;
import com.tk.cratemanagement.domain.OperationLog;
import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;
import com.tk.cratemanagement.dto.InventoryDetailDTO;
import com.tk.cratemanagement.dto.InventorySummaryDTO;
import com.tk.cratemanagement.dto.OperationLogDTO;
import com.tk.cratemanagement.repository.CrateContentRepository;
import com.tk.cratemanagement.repository.CrateRepository;
import com.tk.cratemanagement.repository.OperationLogRepository;
import com.tk.cratemanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存与历史服务实现类
 * 实现实时库存查询和操作历史追溯业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final CrateContentRepository crateContentRepository;
    private final CrateRepository crateRepository;
    private final OperationLogRepository operationLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventorySummaryDTO> getInventorySummary(Long tenantId) {
        log.debug("获取实时库存汇总: tenantId={}", tenantId);
        
        // 查询所有入库状态的周转筐内容
        List<CrateContent> inboundContents = crateContentRepository
                .findByTenantIdAndStatus(tenantId, CrateContentStatus.INBOUND);

        // 按货物分组并汇总数量
        Map<String, List<CrateContent>> groupedByGoods = inboundContents.stream()
                .filter(content -> content.getGoods() != null)
                .collect(Collectors.groupingBy(content -> content.getGoods().getName()));

        return groupedByGoods.entrySet().stream()
                .map(entry -> {
                    String goodsName = entry.getKey();
                    List<CrateContent> contents = entry.getValue();
                    
                    // 获取SKU（假设同一货物的SKU相同）
                    String sku = contents.get(0).getGoods().getSku();
                    
                    // 计算总数量
                    BigDecimal totalQuantity = contents.stream()
                            .map(CrateContent::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new InventorySummaryDTO(goodsName, sku, totalQuantity.doubleValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDetailDTO> getInventoryDetails(Long goodsId, Long tenantId) {
        log.debug("获取货物详细库存: goodsId={}, tenantId={}", goodsId, tenantId);
        
        // 查询指定货物的所有入库周转筐
        List<CrateContent> contents = crateContentRepository
                .findByTenantIdAndGoodsIdAndStatus(tenantId, goodsId, CrateContentStatus.INBOUND);

        return contents.stream()
                .map(this::convertToInventoryDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationLogDTO> getCrateHistory(String nfcUid, Long tenantId) {
        log.debug("获取周转筐操作历史: nfcUid={}, tenantId={}", nfcUid, tenantId);
        
        // 首先找到周转筐
        Crate crate = crateRepository.findByTenantIdAndNfcUid(tenantId, nfcUid)
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在: " + nfcUid));

        // 查询相关的操作日志
        List<OperationLog> logs = operationLogRepository
                .findByTenantIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        tenantId, "CRATE", crate.getId());

        return logs.stream()
                .map(this::convertToOperationLogDTO)
                .collect(Collectors.toList());
    }

    // DTO转换方法
    private InventoryDetailDTO convertToInventoryDetailDTO(CrateContent content) {
        return new InventoryDetailDTO(
                content.getCrate().getId(),
                content.getCrate().getNfcUid(),
                content.getGoods().getName(),
                content.getSupplier() != null ? content.getSupplier().getName() : null,
                content.getBatchNumber(),
                content.getQuantity().doubleValue(),
                content.getStatus(),
                content.getLastUpdatedAt()
        );
    }

    private OperationLogDTO convertToOperationLogDTO(OperationLog log) {
        return new OperationLogDTO(
                log.getId(),
                log.getUserId(),
                log.getEntityType(),
                log.getEntityId(),
                log.getOperationType(),
                null, // payload需要转换为JsonNode，暂时设为null
                log.getCreatedAt()
        );
    }
}