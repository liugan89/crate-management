package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    /**
     * 根据租户ID查找所有供应商
     */
    List<Supplier> findByTenantId(Long tenantId);
    
    /**
     * 根据供应商ID和租户ID查找供应商
     */
    Optional<Supplier> findByIdAndTenantId(Long id, Long tenantId);
}
