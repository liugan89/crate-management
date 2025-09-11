package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating Tenant information
 */
public record TenantUpdateDTO(
        @NotBlank(message = "公司名称不能为空")
        @Size(max = 255, message = "公司名称长度不能超过255个字符")
        String companyName,
        
        @Email(message = "请输入有效的邮箱地址")
        @Size(max = 255, message = "联系邮箱长度不能超过255个字符")
        String contactEmail,
        
        @Size(max = 50, message = "公司电话长度不能超过50个字符")
        String phoneNumber,
        
        @Size(max = 1000, message = "地址长度不能超过1000个字符")
        String address,
        
        @Size(max = 100, message = "城市名称长度不能超过100个字符")
        String city,
        
        @Size(max = 100, message = "省份/州名称长度不能超过100个字符")
        String state,
        
        @Size(max = 20, message = "邮政编码长度不能超过20个字符")
        String zipCode,
        
        @Size(max = 50, message = "国家名称长度不能超过50个字符")
        String country,
        
        @Size(max = 50, message = "时区长度不能超过50个字符")
        String timezone
) {}