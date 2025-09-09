package com.tk.cratemanagement.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OperationDTO(
        @NotBlank String type,
        @NotNull JsonNode payload,
        @NotBlank String clientTimestamp
) {}