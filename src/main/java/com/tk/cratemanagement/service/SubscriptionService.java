package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.ChangePlanRequestDTO;
import com.tk.cratemanagement.dto.InvoiceDTO;
import com.tk.cratemanagement.dto.PlanDTO;
import com.tk.cratemanagement.dto.SubscriptionDTO;
import com.tk.cratemanagement.dto.UsageDTO;

import java.util.List;

/**
 * 订阅与计费服务接口
 * 处理订阅计划、账单历史和用量追踪
 */
public interface SubscriptionService {

    /**
     * 获取当前租户的订阅详情
     *
     * @param tenantId 租户ID
     * @return 订阅DTO
     */
    SubscriptionDTO getCurrentSubscription(Long tenantId);

    /**
     * 获取所有可用的订阅计划
     *
     * @return 计划列表
     */
    List<PlanDTO> getAllPlans();

    /**
     * 更改订阅计划
     * 处理升级或降级订阅
     *
     * @param request 更改计划请求
     * @param tenantId 租户ID
     * @return 更新后的订阅DTO
     */
    SubscriptionDTO changePlan(ChangePlanRequestDTO request, Long tenantId);

    /**
     * 获取租户的账单历史
     *
     * @param tenantId 租户ID
     * @return 发票列表
     */
    List<InvoiceDTO> getInvoiceHistory(Long tenantId);

    /**
     * 检查租户的用量限制
     * 验证当前用量是否超出订阅计划限制
     *
     * @param tenantId 租户ID
     * @param resourceType 资源类型 (users, crates)
     * @param currentCount 当前数量
     * @return 是否允许继续使用
     */
    boolean checkUsageLimit(Long tenantId, String resourceType, int currentCount);

    /**
     * 记录每日用量快照
     * 定时任务调用，记录租户的资源使用情况
     *
     * @param tenantId 租户ID
     */
    void recordDailyUsageSnapshot(Long tenantId);

    /**
     * 获取租户的用量详情
     * 包含当前使用量、限额和使用率等信息
     *
     * @param tenantId 租户ID
     * @return 用量详情DTO
     */
    UsageDTO getTenantUsage(Long tenantId);
}