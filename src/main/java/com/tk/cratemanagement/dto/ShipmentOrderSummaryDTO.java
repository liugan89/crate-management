package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderStatus;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import java.time.ZonedDateTime;

public record ShipmentOrderSummaryDTO(
    Long id,
    String orderNumber,
    ShipmentOrderType type,
    ShipmentOrderStatus status,
    ZonedDateTime createdAt,
    Integer itemCount
) {}
