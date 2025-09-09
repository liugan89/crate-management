package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrateRequestDTO(
    @NotBlank String nfcUid,
    @NotNull Long crateTypeId
) {}
