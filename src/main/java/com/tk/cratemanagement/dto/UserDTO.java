package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.UserRole;
import java.time.LocalDateTime;

public record UserDTO(
    Long id,
    String email,
    String fullName,
    String phone,
    String avatarUrl,
    UserRole role,
    boolean isActive,
    LocalDateTime lastLoginAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
