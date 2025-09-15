package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    /**
     * 根据租户ID查找所有供应商（排除软删除）
     */
    @Query("SELECT s FROM Supplier s WHERE s.tenantId = :tenantId AND s.deletedAt IS NULL")
    List<Supplier> findByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 根据供应商ID和租户ID查找供应商（排除软删除）
     */
    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.tenantId = :tenantId AND s.deletedAt IS NULL")
    Optional<Supplier> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * 根据租户ID和供应商名称查找供应商（排除软删除）
     */
    @Query("SELECT s FROM Supplier s WHERE s.tenantId = :tenantId AND s.name = :name AND s.deletedAt IS NULL")
    Optional<Supplier> findByTenantIdAndName(@Param("tenantId") Long tenantId, @Param("name") String name);
    
    /**
     * 根据租户ID和供应商编码查找供应商（排除软删除）
     */
    @Query("SELECT s FROM Supplier s WHERE s.tenantId = :tenantId AND s.code = :code AND s.deletedAt IS NULL")
    Optional<Supplier> findByTenantIdAndCode(@Param("tenantId") Long tenantId, @Param("code") String code);
    
    /**
     * 根据租户ID和查询条件查找供应商（排除软删除）
     * 支持按名称模糊查询和按激活状态过滤
     */
    @Query(value = "SELECT * FROM suppliers s WHERE s.tenant_id = :tenantId AND s.deleted_at IS NULL " +
           "AND (:name IS NULL OR s.name LIKE '%' || CAST(:name AS VARCHAR) || '%') " +
           "AND (:isActive IS NULL OR s.is_active = :isActive) " +
           "ORDER BY s.created_at DESC", nativeQuery = true)
    List<Supplier> findByTenantIdWithFilters(@Param("tenantId") Long tenantId, 
                                           @Param("name") String name, 
                                           @Param("isActive") Boolean isActive);
}
