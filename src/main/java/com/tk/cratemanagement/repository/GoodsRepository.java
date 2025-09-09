package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Goods entity.
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    
    /**
     * 根据租户ID查找所有货物
     */
    List<Goods> findByTenantId(Long tenantId);
    
    /**
     * 根据货物ID和租户ID查找货物
     */
    Optional<Goods> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * 根据租户ID和SKU查找货物
     */
    Optional<Goods> findByTenantIdAndSku(Long tenantId, String sku);
}
