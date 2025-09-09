package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CrateTypeRequestDTO(
    @NotBlank String name,
    @Positive Double capacity,
    @Positive Double weight
) {}
