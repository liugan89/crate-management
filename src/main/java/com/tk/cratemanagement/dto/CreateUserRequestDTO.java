package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String initialPassword,
        @NotNull UserRole role
) {}
