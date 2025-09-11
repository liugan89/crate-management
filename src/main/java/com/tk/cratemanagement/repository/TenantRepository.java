package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Tenant;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Tenant entity.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    /**
     * 根据公司名称查找租户
     */
    Optional<Tenant> findByCompanyName(String companyName);
    
    /**
     * 根据联系邮箱查找租户
     */
    Optional<Tenant> findByContactEmail(String contactEmail);
    
    /**
     * 根据状态查找租户列表
     */
    List<Tenant> findByStatus(TenantStatus status);
    
    /**
     * 根据城市查找租户列表
     */
    List<Tenant> findByCity(String city);
    
    /**
     * 根据国家查找租户列表
     */
    List<Tenant> findByCountry(String country);
    
    /**
     * 检查公司名称是否已存在（排除指定ID）
     */
    @Query("SELECT COUNT(t) > 0 FROM Tenant t WHERE t.companyName = :companyName AND (:excludeId IS NULL OR t.id != :excludeId)")
    boolean existsByCompanyNameAndIdNot(@Param("companyName") String companyName, @Param("excludeId") Long excludeId);
    
    /**
     * 检查联系邮箱是否已存在（排除指定ID）
     */
    @Query("SELECT COUNT(t) > 0 FROM Tenant t WHERE t.contactEmail = :contactEmail AND (:excludeId IS NULL OR t.id != :excludeId)")
    boolean existsByContactEmailAndIdNot(@Param("contactEmail") String contactEmail, @Param("excludeId") Long excludeId);
}
