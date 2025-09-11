package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationRequestDTO(
    @NotBlank(message = "库位名称不能为空")
    @Size(max = 255, message = "库位名称长度不能超过255个字符")
    String name,
    
    @Size(max = 100, message = "库位编码长度不能超过100个字符")
    String code,
    
    String description,
    
    @Size(max = 100, message = "区域名称长度不能超过100个字符")
    String zone,
    
    Boolean isActive
) {}