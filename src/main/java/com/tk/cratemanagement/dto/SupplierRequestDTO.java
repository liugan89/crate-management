package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(
    @NotBlank(message = "供应商名称不能为空")
    @Size(max = 255, message = "供应商名称长度不能超过255个字符")
    String name,
    
    @Size(max = 100, message = "供应商编码长度不能超过100个字符")
    String code,
    
    @Size(max = 255, message = "联系人姓名长度不能超过255个字符")
    String contactName,
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    String contactEmail,
    
    @Size(max = 50, message = "联系电话长度不能超过50个字符")
    String contactPhone,
    
    String address,
    
    @Size(max = 100, message = "城市名称长度不能超过100个字符")
    String city,
    
    @Size(max = 100, message = "省份名称长度不能超过100个字符")
    String state,
    
    @Size(max = 50, message = "邮编长度不能超过50个字符")
    String zipCode,
    
    @Size(max = 100, message = "国家名称长度不能超过100个字符")
    String country,
    
    Boolean isActive
) {}
