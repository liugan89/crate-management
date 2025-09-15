package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateScanRequestDTO(
        @NotBlank String nfcUid,
        @Positive BigDecimal actualQuantity,
        @NotNull Long locationId
) {}
