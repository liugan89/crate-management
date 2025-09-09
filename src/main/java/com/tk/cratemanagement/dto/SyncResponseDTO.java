package com.tk.cratemanagement.dto;

import java.util.List;

public record SyncResponseDTO(
        boolean success,
        List<String> errors,
        int processedCount,
        String serverTimestamp
) {}