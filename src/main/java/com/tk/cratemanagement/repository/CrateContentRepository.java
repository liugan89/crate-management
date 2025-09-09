package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.CrateContent;
import com.tk.cratemanagement.domain.enumeration.CrateContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the CrateContent entity.
 */
@Repository
public interface CrateContentRepository extends JpaRepository<CrateContent, Long> {
    
    Optional<CrateContent> findByCrateId(Long crateId);
    
    /**
     * 根据租户ID和状态查找周转筐内容
     */
    List<CrateContent> findByTenantIdAndStatus(Long tenantId, CrateContentStatus status);
    
    /**
     * 根据租户ID、货物ID和状态查找周转筐内容
     */
    List<CrateContent> findByTenantIdAndGoodsIdAndStatus(Long tenantId, Long goodsId, CrateContentStatus status);
}
