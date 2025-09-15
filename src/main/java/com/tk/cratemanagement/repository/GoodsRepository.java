package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Goods entity.
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    
    /**
     * 根据租户ID查找所有货物（排除软删除）
     */
    @Query("SELECT g FROM Goods g WHERE g.tenantId = :tenantId AND g.deletedAt IS NULL")
    List<Goods> findByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 根据货物ID和租户ID查找货物（排除软删除）
     */
    @Query("SELECT g FROM Goods g WHERE g.id = :id AND g.tenantId = :tenantId AND g.deletedAt IS NULL")
    Optional<Goods> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * 根据租户ID和SKU查找货物（排除软删除）
     */
    @Query("SELECT g FROM Goods g WHERE g.tenantId = :tenantId AND g.sku = :sku AND g.deletedAt IS NULL")
    Optional<Goods> findByTenantIdAndSku(@Param("tenantId") Long tenantId, @Param("sku") String sku);
    
    /**
     * 根据租户ID和查询条件查找货物
     * 支持按名称模糊查询和按激活状态过滤
     */
    @Query(value = "SELECT * FROM goods g WHERE g.tenant_id = :tenantId AND g.deleted_at IS NULL " +
           "AND (:name IS NULL OR g.name LIKE '%' || CAST(:name AS VARCHAR) || '%') " +
           "AND (:isActive IS NULL OR g.is_active = :isActive) " +
           "ORDER BY g.created_at DESC", nativeQuery = true)
    List<Goods> findByTenantIdWithFilters(@Param("tenantId") Long tenantId, 
                                         @Param("name") String name, 
                                         @Param("isActive") Boolean isActive);
}
