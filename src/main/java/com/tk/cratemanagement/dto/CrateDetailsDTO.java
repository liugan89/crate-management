package com.tk.cratemanagement.dto;

public record CrateDetailsDTO(
    CrateDTO crateInfo,
    CrateContentDTO currentContent
) {}
