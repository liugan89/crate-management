package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderStatus;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import java.time.LocalDateTime;

public record ShipmentOrderDTO(
    Long id,
    String orderNumber,
    ShipmentOrderType type,
    ShipmentOrderStatus status,
    String priority,
    String notes,
    LocalDateTime expectedDeliveryDate,
    LocalDateTime actualDeliveryDate,
    Long createdByUserId,
    Long completedByUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime completedAt
) {}
