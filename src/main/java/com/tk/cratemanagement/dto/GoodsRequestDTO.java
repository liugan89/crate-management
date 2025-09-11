package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ProductUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoodsRequestDTO(
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 255, message = "商品名称长度不能超过255个字符")
    String name,
    
    @Size(max = 100, message = "SKU长度不能超过100个字符")
    String sku,
    
    @Size(max = 100, message = "条形码长度不能超过100个字符")
    String barcode,
    
    ProductUnit unit,
    
    @Size(max = 100, message = "分类长度不能超过100个字符")
    String category,
    
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    String imageUrl,
    
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    String description,
    
    String customFields,
    
    Boolean isActive
) {}
