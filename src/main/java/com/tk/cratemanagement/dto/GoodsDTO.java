package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ProductUnit;
import java.time.ZonedDateTime;

public record GoodsDTO(
    Long id,
    String name,
    String sku,
    String barcode,
    ProductUnit unit,
    String category,
    String imageUrl,
    String description,
    String customFields,
    Boolean isActive,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {}
