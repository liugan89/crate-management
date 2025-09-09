package com.tk.cratemanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

/**
 * Represents a subscription plan.
 */
@Getter
@Setter
@Entity
@Table(name = "plans")
@Audited
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "price_monthly", precision = 10, scale = 2)
    private BigDecimal priceMonthly;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quotas", columnDefinition = "jsonb")
    private String quotas;
}