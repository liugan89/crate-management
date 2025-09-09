package com.tk.cratemanagement.dto;

public record InventorySummaryDTO(
        String goodsName,
        String sku,
        double totalQuantity
) {}