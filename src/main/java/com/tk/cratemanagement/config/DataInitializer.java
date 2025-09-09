package com.tk.cratemanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tk.cratemanagement.domain.Plan;
import com.tk.cratemanagement.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数据初始化器
 * 在应用启动时初始化必要的基础数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        initializePlans();
    }

    /**
     * 初始化订阅计划
     */
    private void initializePlans() {
        try {
        // 检查是否已存在TRIAL计划
        if (planRepository.findByName("TRIAL").isEmpty()) {
            Plan trialPlan = new Plan();
            trialPlan.setName("TRIAL");
            trialPlan.setPriceMonthly(BigDecimal.ZERO);
            // 设置试用计划的配额（可根据需要调整）
            trialPlan.setQuotas(objectMapper.writeValueAsString(Map.of("maxUsers", 5, "maxCrates", 100)));
            planRepository.save(trialPlan);
            log.info("已创建TRIAL订阅计划");
        }

        // 可以添加其他基础计划
        if (planRepository.findByName("BASIC").isEmpty()) {
            Plan basicPlan = new Plan();
            basicPlan.setName("BASIC");
            basicPlan.setPriceMonthly(new BigDecimal("99.00"));
            basicPlan.setQuotas(objectMapper.writeValueAsString(Map.of("maxUsers", 20, "maxCrates", 1000)));
            planRepository.save(basicPlan);
            log.info("已创建BASIC订阅计划");
        }

        if (planRepository.findByName("PREMIUM").isEmpty()) {
            Plan premiumPlan = new Plan();
            premiumPlan.setName("PREMIUM");
            premiumPlan.setPriceMonthly(new BigDecimal("299.00"));
            premiumPlan.setQuotas(objectMapper.writeValueAsString(Map.of("maxUsers", 100, "maxCrates", 10000)));
            planRepository.save(premiumPlan);
            log.info("已创建PREMIUM订阅计划");
        }
        } catch (Exception e) {
            log.error("初始化订阅计划失败: {}", e.getMessage(), e);
        }
    }
}