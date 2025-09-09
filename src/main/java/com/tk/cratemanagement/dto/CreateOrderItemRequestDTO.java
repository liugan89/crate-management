package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateOrderItemRequestDTO(
    @NotNull Long goodsId,
    Long supplierId,
    @NotNull @Positive Double expectedQuantity,
    String batchNumber,
    LocalDate productionDate
) {}
