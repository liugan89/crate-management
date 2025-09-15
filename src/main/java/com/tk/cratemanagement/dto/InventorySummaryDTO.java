package com.tk.cratemanagement.dto;

import java.math.BigDecimal;

public record InventorySummaryDTO<Stirng>(
        Long goodsId,
        String goodsName,
        String sku,
        BigDecimal totalQuantity
) {}