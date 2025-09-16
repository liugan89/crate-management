package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.UsageSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Spring Data JPA repository for the UsageSnapshot entity.
 */
@Repository
public interface UsageSnapshotRepository extends JpaRepository<UsageSnapshot, Long> {
    
    /**
     * 根据租户ID和快照日期查找用量快照
     */
    Optional<UsageSnapshot> findByTenantIdAndSnapshotDate(Long tenantId, LocalDate snapshotDate);
    
    /**
     * 根据租户ID查找最新的用量快照
     */
    Optional<UsageSnapshot> findTopByTenantIdOrderBySnapshotDateDesc(Long tenantId);
}
