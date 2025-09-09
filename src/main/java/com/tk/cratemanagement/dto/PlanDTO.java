package com.tk.cratemanagement.dto;

import java.math.BigDecimal;

public record PlanDTO(
    Long id,
    String name,
    BigDecimal price,
    int maxUsers,
    int maxCrates,
    String description
) {}
