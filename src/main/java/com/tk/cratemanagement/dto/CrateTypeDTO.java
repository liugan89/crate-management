package com.tk.cratemanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CrateTypeDTO(
    Long id,
    Long tenantId,
    String name,
    BigDecimal capacity,
    BigDecimal weight,
    String dimensions,
    String material,
    String color,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
