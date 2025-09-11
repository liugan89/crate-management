package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.TenantStatus;

import java.time.LocalDateTime;

/**
 * DTO for Tenant entity
 */
public record TenantDTO(
        Long id,
        String companyName,
        String contactEmail,
        String phoneNumber,
        String address,
        String city,
        String state,
        String zipCode,
        String country,
        String timezone,
        TenantStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}