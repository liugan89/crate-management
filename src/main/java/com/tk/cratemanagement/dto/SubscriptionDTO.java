package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.SubscriptionStatus;
import java.time.LocalDate;

public record SubscriptionDTO(
    String planName,
    SubscriptionStatus status,
    int userCount,
    int crateCount,
    int maxUsers,
    int maxCrates,
    LocalDate nextBillingDate
) {}
