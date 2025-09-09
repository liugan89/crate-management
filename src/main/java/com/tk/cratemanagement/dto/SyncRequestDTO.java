package com.tk.cratemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SyncRequestDTO(
        @NotEmpty @Valid List<OperationDTO> operations
) {}