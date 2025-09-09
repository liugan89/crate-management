package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationRequestDTO(
        @NotBlank String name
) {}