package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.ChangePlanRequestDTO;
import com.tk.cratemanagement.dto.InvoiceDTO;
import com.tk.cratemanagement.dto.PlanDTO;
import com.tk.cratemanagement.dto.SubscriptionDTO;
import com.tk.cratemanagement.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订阅与计费控制器
 * 处理订阅管理和计费相关的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "订阅与计费", description = "订阅计划和计费管理相关API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * 获取当前租户的订阅详情
     */
    @GetMapping("/subscription")
    @Operation(summary = "获取订阅详情", description = "获取当前租户的订阅和用量详情")
    public ResponseEntity<SubscriptionDTO> getCurrentSubscription(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取订阅详情: tenantId={}", tenantId);
        
        SubscriptionDTO subscription = subscriptionService.getCurrentSubscription(tenantId);
        
        return ResponseEntity.ok(subscription);
    }

    /**
     * 获取所有可用的订阅计划
     */
    @GetMapping("/subscription/plans")
    @Operation(summary = "获取订阅计划", description = "获取所有可用的订阅计划")
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        log.debug("获取所有订阅计划");
        
        List<PlanDTO> plans = subscriptionService.getAllPlans();
        
        return ResponseEntity.ok(plans);
    }

    /**
     * 更改订阅计划
     * 处理升级或降级订阅
     */
    @PostMapping("/subscription/change-plan")
    @Operation(summary = "更改订阅计划", description = "升级或更改当前订阅计划")
    public ResponseEntity<SubscriptionDTO> changePlan(@Valid @RequestBody ChangePlanRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到更改订阅计划请求: tenantId={}, planId={}", tenantId, request.planId());
        
        SubscriptionDTO response = subscriptionService.changePlan(request, tenantId);
        
        log.info("订阅计划更改成功: tenantId={}", tenantId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取租户的账单历史
     */
    @GetMapping("/invoices")
    @Operation(summary = "获取账单历史", description = "获取当前租户的计费历史")
    public ResponseEntity<List<InvoiceDTO>> getInvoiceHistory(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取账单历史: tenantId={}", tenantId);
        
        List<InvoiceDTO> invoices = subscriptionService.getInvoiceHistory(tenantId);
        
        return ResponseEntity.ok(invoices);
    }

    /**
     * 从认证信息中提取租户ID
     */
    private Long getTenantIdFromAuth(Authentication authentication) {
        // TODO: 从JWT token中提取租户ID
        return 1L; // 临时返回
    }
}