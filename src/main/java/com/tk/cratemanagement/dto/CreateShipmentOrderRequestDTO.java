package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import jakarta.validation.constraints.NotNull;

public record CreateShipmentOrderRequestDTO(
        @NotNull ShipmentOrderType type,
        String notes,
        Long originalOrderId
) {}
