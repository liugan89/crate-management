package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateStatus;

public record CrateDTO(
    Long id,
    String nfcUid,
    CrateStatus status,
    Long crateTypeId,
    String crateTypeName
) {}
