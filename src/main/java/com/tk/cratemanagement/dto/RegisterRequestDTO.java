package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank String companyName,
        @Email @NotBlank String email,
        @Size(min = 8) String password
) {}
