package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CrateContentDTO(
    Long id,
    Long goodsId,
    String goodsName,
    BigDecimal quantity,
    CrateContentStatus status,
    Long locationId,
    String locationName,
    Instant lastUpdatedAt
) {}
