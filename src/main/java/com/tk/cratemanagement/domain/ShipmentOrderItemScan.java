package com.tk.cratemanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents an NFC scan record for a shipment order item.
 */
@Getter
@Setter
@Entity
@Table(name = "shipment_order_item_scans")
@Audited
public class ShipmentOrderItemScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private ShipmentOrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crate_id", nullable = false)
    private Crate crate;

    @Column(name = "actual_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualQuantity;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private Instant scannedAt = Instant.now();

    @Column(name = "scanned_by_user_id", nullable = false)
    private Long scannedByUserId;
}