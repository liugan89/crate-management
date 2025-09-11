package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String initialPassword,
        @NotNull UserRole role,
        @Size(max = 255, message = "姓名长度不能超过255个字符")
        String fullName,
        @Size(max = 50, message = "手机号长度不能超过50个字符")
        String phone
) {}
