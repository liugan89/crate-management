package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;

public record CrateContentDTO(
    Long id,
    Long goodsId,
    String goodsName,
    Double quantity,
    CrateContentStatus status
) {}
