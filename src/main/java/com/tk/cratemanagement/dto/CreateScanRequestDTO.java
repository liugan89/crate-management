package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateScanRequestDTO(
        @NotBlank String nfcUid,
        @Positive double actualQuantity
) {}
