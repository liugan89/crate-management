package com.tk.cratemanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

/**
 * Represents a type/template of crate with physical properties.
 */
@Getter
@Setter
@Entity
@Table(name = "crate_types")
@Audited
public class CrateType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;
}