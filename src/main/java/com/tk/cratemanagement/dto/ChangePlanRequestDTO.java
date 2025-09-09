package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotNull;

public record ChangePlanRequestDTO(
        @NotNull Long planId,
        String paymentToken
) {}
