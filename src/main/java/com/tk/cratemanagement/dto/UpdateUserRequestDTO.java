package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @Size(max = 255, message = "姓名长度不能超过255个字符")
        String fullName,
        @Size(max = 50, message = "手机号长度不能超过50个字符")
        String phone,
        UserRole role,
        Boolean isActive
) {}
