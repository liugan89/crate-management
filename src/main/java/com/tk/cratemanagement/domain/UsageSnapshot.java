package com.tk.cratemanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

/**
 * Represents a daily usage snapshot for a tenant.
 */
@Getter
@Setter
@Entity
@Table(name = "usage_snapshots", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tenant_id", "snapshot_date"})
})
@Audited
public class UsageSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "user_count", nullable = false)
    private Integer userCount;

    @Column(name = "crate_count", nullable = false)
    private Integer crateCount;
}