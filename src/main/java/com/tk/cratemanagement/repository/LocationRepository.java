package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Location entity.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * 根据租户ID查找所有库位（排除软删除）
     */
    @Query("SELECT l FROM Location l WHERE l.tenantId = :tenantId AND l.deletedAt IS NULL")
    List<Location> findByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 根据库位ID和租户ID查找库位（排除软删除）
     */
    @Query("SELECT l FROM Location l WHERE l.id = :id AND l.tenantId = :tenantId AND l.deletedAt IS NULL")
    Optional<Location> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * 根据租户ID和库位名称查找库位（排除软删除）
     */
    @Query("SELECT l FROM Location l WHERE l.tenantId = :tenantId AND l.name = :name AND l.deletedAt IS NULL")
    Optional<Location> findByTenantIdAndName(@Param("tenantId") Long tenantId, @Param("name") String name);
    
    /**
     * 根据租户ID和库位编码查找库位（排除软删除）
     */
    @Query("SELECT l FROM Location l WHERE l.tenantId = :tenantId AND l.code = :code AND l.deletedAt IS NULL")
    Optional<Location> findByTenantIdAndCode(@Param("tenantId") Long tenantId, @Param("code") String code);
    
    /**
     * 根据租户ID和查询条件查找库位（排除软删除）
     * 支持按名称模糊查询和按激活状态过滤
     */
    @Query(value = "SELECT * FROM locations l WHERE l.tenant_id = :tenantId AND l.deleted_at IS NULL " +
           "AND (:name IS NULL OR l.name LIKE '%' || CAST(:name AS VARCHAR) || '%') " +
           "AND (:isActive IS NULL OR l.is_active = :isActive) " +
           "ORDER BY l.created_at DESC", nativeQuery = true)
    List<Location> findByTenantIdWithFilters(@Param("tenantId") Long tenantId, 
                                           @Param("name") String name, 
                                           @Param("isActive") Boolean isActive);
}
