package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;

public record GoodsRequestDTO(
    @NotBlank String name,
    String sku,
    String description
) {}
