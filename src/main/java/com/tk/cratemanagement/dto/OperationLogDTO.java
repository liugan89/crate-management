package com.tk.cratemanagement.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record OperationLogDTO(
        Long id,
        Long userId,
        String entityType,
        Long entityId,
        String operationType,
        JsonNode payload,
        Instant createdAt
) {}