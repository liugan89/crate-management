package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;

public record UpdateUserRequestDTO(
        UserRole role,
        Boolean isActive
) {}
