package com.tk.cratemanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用量查询DTO
 * 用于返回租户的资源使用情况
 */
public record UsageDTO(
    int currentUserCount,
    int currentCrateCount,
    int maxUsers,
    int maxCrates,
    String planName,
    LocalDate lastSnapshotDate,
    BigDecimal userUtilizationRate,
    BigDecimal crateUtilizationRate
) {}