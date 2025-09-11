package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.InventoryDetailDTO;
import com.tk.cratemanagement.dto.InventorySummaryDTO;
import com.tk.cratemanagement.dto.OperationLogDTO;
import com.tk.cratemanagement.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存与历史控制器
 * 处理实时库存查询和操作历史追溯的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "库存与历史", description = "实时库存查询和操作历史追溯相关API")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 获取实时库存汇总
     * 按货物分组的库存总览
     */
    @GetMapping("/inventory/summary")
    @Operation(summary = "获取库存汇总", description = "获取按货物分组的实时库存汇总")
    public ResponseEntity<List<InventorySummaryDTO>> getInventorySummary(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取库存汇总: tenantId={}", tenantId);
        
        List<InventorySummaryDTO> summary = inventoryService.getInventorySummary(tenantId);
        
        return ResponseEntity.ok(summary);
    }

    /**
     * 获取指定货物的详细库存
     * 显示所有包含该货物的周转筐详情
     */
    @GetMapping("/inventory/details")
    @Operation(summary = "获取库存详情", description = "获取指定货物的详细库存信息")
    public ResponseEntity<List<InventoryDetailDTO>> getInventoryDetails(
            @RequestParam Long goods_id,
            Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取库存详情: goodsId={}, tenantId={}", goods_id, tenantId);
        
        List<InventoryDetailDTO> details = inventoryService.getInventoryDetails(goods_id, tenantId);
        
        return ResponseEntity.ok(details);
    }

    /**
     * 获取周转筐的操作历史
     * 追溯单个周转筐的完整生命周期
     */
    @GetMapping("/history/crates")
    @Operation(summary = "获取周转筐历史", description = "获取指定周转筐的完整操作历史")
    public ResponseEntity<List<OperationLogDTO>> getCrateHistory(
            @RequestParam @NotBlank String nfc_uid,
            Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取周转筐历史: nfcUid={}, tenantId={}", nfc_uid, tenantId);
        
        List<OperationLogDTO> history = inventoryService.getCrateHistory(nfc_uid, tenantId);
        
        return ResponseEntity.ok(history);
    }

    /**
     * 从认证信息中提取租户ID
     */
    private Long getTenantIdFromAuth(Authentication authentication) {
        // 使用AuthUtils工具类从认证信息中提取租户ID
        return com.tk.cratemanagement.util.AuthUtils.getTenantIdFromAuth(authentication);
    }
}