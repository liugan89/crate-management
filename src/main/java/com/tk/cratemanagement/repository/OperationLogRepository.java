package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the OperationLog entity.
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    /**
     * 根据租户ID查找操作日志
     */
    List<OperationLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    
    /**
     * 根据租户ID和实体类型查找操作日志
     */
    List<OperationLog> findByTenantIdAndEntityTypeOrderByCreatedAtDesc(Long tenantId, String entityType);
    
    /**
     * 根据租户ID、实体类型和实体ID查找操作日志
     */
    List<OperationLog> findByTenantIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(Long tenantId, String entityType, Long entityId);
    
    /**
     * 根据周转筐NFC UID查找操作历史（用于追溯）
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.tenantId = :tenantId AND " +
           "((ol.entityType = 'Crate' AND ol.entityId = (SELECT c.id FROM Crate c WHERE c.tenantId = :tenantId AND c.nfcUid = :nfcUid)) OR " +
           "(ol.entityType = 'CrateContent' AND ol.entityId = (SELECT cc.id FROM CrateContent cc JOIN cc.crate c WHERE c.tenantId = :tenantId AND c.nfcUid = :nfcUid))) " +
           "ORDER BY ol.createdAt DESC")
    List<OperationLog> findCrateHistoryByNfcUid(@Param("tenantId") Long tenantId, @Param("nfcUid") String nfcUid);
}
