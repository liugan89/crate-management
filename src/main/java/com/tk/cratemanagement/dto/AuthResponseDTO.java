package com.tk.cratemanagement.dto;

public record AuthResponseDTO(
//        Long tenantId,
        String token,
        String role,
        Long userId

) {}
