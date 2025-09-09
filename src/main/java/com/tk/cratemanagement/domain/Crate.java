package com.tk.cratemanagement.domain;

import com.tk.cratemanagement.domain.enumeration.CrateStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

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
}