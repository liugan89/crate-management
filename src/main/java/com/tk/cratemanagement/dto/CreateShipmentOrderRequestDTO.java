package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateShipmentOrderRequestDTO(
        @NotNull ShipmentOrderType type,
        String priority,
        String notes,
        LocalDateTime expectedDeliveryDate,
        Long originalOrderId
) {}
