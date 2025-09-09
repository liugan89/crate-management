package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderStatus;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import java.time.ZonedDateTime;
import java.util.List;

public record ShipmentOrderDetailsDTO(
    Long id,
    ShipmentOrderType type,
    ShipmentOrderStatus status,
    ZonedDateTime createdAt,
    ZonedDateTime completedAt,
    String notes,
    List<OrderItemDTO> items
) {}
