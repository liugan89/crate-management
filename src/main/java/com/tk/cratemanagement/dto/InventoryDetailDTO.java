package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;

import java.time.Instant;

public record InventoryDetailDTO(
        Long crateId,
        String nfcUid,
        String goodsName,
        String supplierName,
        String batchNumber,
        double quantity,
        CrateContentStatus status,
        Instant lastUpdatedAt
) {}