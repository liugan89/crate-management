package com.tk.cratemanagement.domain;

import com.tk.cratemanagement.domain.enumeration.ShipmentOrderStatus;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shipment order for receiving or sending goods.
 */
@Getter
@Setter
@Entity
@Table(name = "shipment_orders", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "order_number"})
})
@Audited
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Filter(name = "softDeleteFilter", condition = "deleted_at IS NULL")
public class ShipmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "order_number", nullable = false, length = 100)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentOrderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentOrderStatus status;

    @Column(length = 20)
    private String priority = "NORMAL";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_user_id")
    private User completedByUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "shipmentOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentOrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}