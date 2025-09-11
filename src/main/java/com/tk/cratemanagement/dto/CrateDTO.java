package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CrateDTO(
    Long id,
    String nfcUid,
    CrateStatus status,
    Long crateTypeId,
    String crateTypeName,
    Long lastKnownLocationId,
    String lastKnownLocationName,
    LocalDateTime lastSeenAt,
    LocalDate maintenanceDueDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
