package com.tk.cratemanagement.domain;

import com.tk.cratemanagement.domain.enumeration.CrateStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a physical crate with an NFC tag.
 */
@Getter
@Setter
@Entity
@Table(name = "crates", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "nfc_uid"})
})
@Audited
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Filter(name = "softDeleteFilter", condition = "deleted_at IS NULL")
public class Crate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "nfc_uid", nullable = false)
    private String nfcUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crate_type_id")
    private CrateType crateType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_known_location_id")
    private Location lastKnownLocation;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "maintenance_due_date")
    private LocalDate maintenanceDueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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