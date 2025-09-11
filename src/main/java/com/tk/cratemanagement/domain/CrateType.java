package com.tk.cratemanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a type/template of crate with physical properties.
 */
@Getter
@Setter
@Entity
@Table(name = "crate_types")
@Audited
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Filter(name = "softDeleteFilter", condition = "deleted_at IS NULL")
public class CrateType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column
    private String dimensions;

    @Column
    private String material;

    @Column
    private String color;

    @Column(name = "is_active")
    private Boolean isActive = true;

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