package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDTO(
    @NotBlank String name,
    String contactPerson,
    String contactPhone,
    String address
) {}
