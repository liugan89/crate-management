package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Crate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Crate entity.
 */
@Repository
public interface CrateRepository extends JpaRepository<Crate, Long> {
    
    Optional<Crate> findByTenantIdAndNfcUid(Long tenantId, String nfcUid);
    
    /**
     * 根据租户ID查找所有周转筐
     */
    List<Crate> findByTenantId(Long tenantId);
    
    /**
     * 根据周转筐ID和租户ID查找周转筐
     */
    Optional<Crate> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * 统计租户下的周转筐数量
     */
    int countByTenantId(Long tenantId);
    
    /**
     * 检查是否有周转筐正在使用指定的周转筐类型
     */
    boolean existsByCrateTypeId(Long crateTypeId);
}
