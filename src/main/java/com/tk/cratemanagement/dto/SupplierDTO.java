package com.tk.cratemanagement.dto;

public record SupplierDTO(
    Long id,
    String name,
    String contactPerson,
    String contactPhone,
    String address
) {}
