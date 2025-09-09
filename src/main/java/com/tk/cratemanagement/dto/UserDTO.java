package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;

public record UserDTO(
        Long id,
        String email,
        UserRole role,
        boolean isActive
) {}
