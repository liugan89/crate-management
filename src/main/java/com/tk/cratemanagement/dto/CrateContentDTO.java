package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;
import java.time.Instant;

public record CrateContentDTO(
    Long id,
    Long goodsId,
    String goodsName,
    Double quantity,
    CrateContentStatus status,
    Long locationId,
    String locationName,
    Instant lastUpdatedAt
) {}
