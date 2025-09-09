package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Location entity.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * 根据租户ID查找所有库位
     */
    List<Location> findByTenantId(Long tenantId);
    
    /**
     * 根据库位ID和租户ID查找库位
     */
    Optional<Location> findByIdAndTenantId(Long id, Long tenantId);
}
