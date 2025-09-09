package com.tk.cratemanagement.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.cratemanagement.domain.*;
import com.tk.cratemanagement.dto.ChangePlanRequestDTO;
import com.tk.cratemanagement.dto.InvoiceDTO;
import com.tk.cratemanagement.dto.PlanDTO;
import com.tk.cratemanagement.dto.SubscriptionDTO;
import com.tk.cratemanagement.repository.*;
import com.tk.cratemanagement.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订阅与计费服务实现类
 * 实现订阅管理、计费和用量追踪业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsageSnapshotRepository usageSnapshotRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CrateRepository crateRepository;

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getCurrentSubscription(Long tenantId) {
        log.debug("获取租户订阅信息: tenantId={}", tenantId);
        
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("订阅不存在"));
        
        return convertToSubscriptionDTO(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanDTO> getAllPlans() {
        log.debug("获取所有订阅计划");
        
        List<Plan> plans = planRepository.findAll();
        return plans.stream()
                .map(this::convertToPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionDTO changePlan(ChangePlanRequestDTO request, Long tenantId) {
        log.info("更改订阅计划: tenantId={}, planId={}", tenantId, request.planId());
        
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("订阅不存在"));

        Plan newPlan = planRepository.findById(request.planId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("订阅计划不存在"));

        // TODO: 集成Stripe支付逻辑
        // 这里应该调用Stripe API处理支付
        log.info("处理支付: paymentToken={}", request.paymentToken());

        subscription.setPlan(newPlan);
        subscription = subscriptionRepository.save(subscription);

        log.info("订阅计划更改成功: tenantId={}, newPlanId={}", tenantId, newPlan.getId());
        return convertToSubscriptionDTO(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoiceHistory(Long tenantId) {
        log.debug("获取租户账单历史: tenantId={}", tenantId);
        
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("订阅不存在"));

        List<Invoice> invoices = invoiceRepository.findBySubscriptionIdOrderByCreatedAtDesc(subscription.getId());
        return invoices.stream()
                .map(this::convertToInvoiceDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkUsageLimit(Long tenantId, String resourceType, int currentCount) {
        log.debug("检查用量限制: tenantId={}, resourceType={}, currentCount={}", 
                tenantId, resourceType, currentCount);
        
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("订阅不存在"));

        Plan plan = subscription.getPlan();
        if (plan.getQuotas() == null || plan.getQuotas().isEmpty()) {
            // 无限制
            return true;
        }

        // 从JSON中解析配额限制
        try {
            Map<String, Object> quotas = objectMapper.readValue(plan.getQuotas(), new TypeReference<Map<String, Object>>() {});
            Integer limit = (Integer) quotas.get(resourceType);
        
        if (limit == null) {
            // 该资源类型无限制
            return true;
        }

            boolean allowed = currentCount < limit;
            log.debug("用量检查结果: allowed={}, current={}, limit={}", allowed, currentCount, limit);
            
            return allowed;
        } catch (Exception e) {
            log.error("解析配额配置失败: {}", e.getMessage());
            return true; // 解析失败时允许访问
        }
    }

    @Override
    @Transactional
    public void recordDailyUsageSnapshot(Long tenantId) {
        log.debug("记录每日用量快照: tenantId={}", tenantId);
        
        LocalDate today = LocalDate.now();
        
        // 检查今日是否已记录
        if (usageSnapshotRepository.findByTenantIdAndSnapshotDate(tenantId, today).isPresent()) {
            log.debug("今日用量快照已存在: tenantId={}, date={}", tenantId, today);
            return;
        }

        // 统计当前用量
        int userCount = userRepository.countByTenantId(tenantId);
        int crateCount = crateRepository.countByTenantId(tenantId);

        UsageSnapshot snapshot = new UsageSnapshot();
        snapshot.setTenantId(tenantId);
        snapshot.setSnapshotDate(today);
        snapshot.setUserCount(userCount);
        snapshot.setCrateCount(crateCount);

        usageSnapshotRepository.save(snapshot);
        log.info("每日用量快照记录成功: tenantId={}, userCount={}, crateCount={}", 
                tenantId, userCount, crateCount);
    }

    // DTO转换方法
    private SubscriptionDTO convertToSubscriptionDTO(Subscription subscription) {
        // 获取当前用量
        int userCount = userRepository.countByTenantId(subscription.getTenantId());
        int crateCount = crateRepository.countByTenantId(subscription.getTenantId());
        
        return new SubscriptionDTO(
                subscription.getPlan().getName(),
                subscription.getStatus(),
                userCount,
                crateCount,
                100, // maxUsers暂时硬编码
                1000, // maxCrates暂时硬编码
                subscription.getCurrentPeriodEnd().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        );
    }

    private PlanDTO convertToPlanDTO(Plan plan) {
        return new PlanDTO(
                plan.getId().longValue(),
                plan.getName(),
                plan.getPriceMonthly(),
                0, // maxUsers暂时设为0
                0, // maxCrates暂时设为0
                null // description暂时设为null
        );
    }

    private InvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        return new InvoiceDTO(
                invoice.getId(),
                LocalDate.now(), // issueDate暂时设为当前日期
                LocalDate.now().plusDays(30), // dueDate暂时设为30天后
                invoice.getAmount(),
                invoice.getStatus()
        );
    }
}