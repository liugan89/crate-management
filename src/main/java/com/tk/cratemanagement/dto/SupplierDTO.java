package com.tk.cratemanagement.dto;

import java.time.LocalDateTime;

public record SupplierDTO(
    Long id,
    String name,
    String code,
    String contactName,
    String contactEmail,
    String contactPhone,
    String address,
    String city,
    String state,
    String zipCode,
    String country,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
