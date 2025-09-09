package com.tk.cratemanagement.dto;

public record CrateTypeDTO(
    Long id,
    String name,
    Double capacity,
    Double weight
) {}
