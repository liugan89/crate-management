package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateScanRequestDTO(
    @Positive BigDecimal actualQuantity,
    @NotNull Long locationId
) {}