package com.tk.cratemanagement.dto;

import java.time.LocalDateTime;

public record LocationDTO(
    Long id,
    String name,
    String code,
    String description,
    String zone,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}