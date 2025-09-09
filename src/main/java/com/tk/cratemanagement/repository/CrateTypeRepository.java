package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.CrateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the CrateType entity.
 */
@Repository
public interface CrateTypeRepository extends JpaRepository<CrateType, Long> {
    
    /**
     * 根据租户ID查找所有周转筐类型
     */
    List<CrateType> findByTenantId(Long tenantId);
    
    /**
     * 根据周转筐类型ID和租户ID查找周转筐类型
     */
    Optional<CrateType> findByIdAndTenantId(Long id, Long tenantId);
}
