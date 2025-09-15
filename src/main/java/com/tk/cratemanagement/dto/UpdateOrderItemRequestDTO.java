package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateOrderItemRequestDTO(
    Long goodsId,
    Long supplierId,
    @Positive BigDecimal expectedQuantity,
    String batchNumber,
    LocalDate productionDate
) {}