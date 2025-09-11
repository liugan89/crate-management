package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CrateTypeRequestDTO(
    @NotBlank(message = "周转筐类型名称不能为空")
    @Size(max = 255, message = "周转筐类型名称长度不能超过255个字符")
    String name,
    
    @Size(max = 100, message = "类型编码长度不能超过100个字符")
    String code,
    
    BigDecimal capacity,
    
    BigDecimal weight,
    
    @Size(max = 100, message = "尺寸描述长度不能超过100个字符")
    String dimensions,
    
    @Size(max = 100, message = "材质长度不能超过100个字符")
    String material,
    
    @Size(max = 50, message = "颜色长度不能超过50个字符")
    String color,
    
    Boolean isActive
) {}