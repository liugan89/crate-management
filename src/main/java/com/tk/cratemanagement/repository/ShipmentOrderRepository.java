package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.ShipmentOrder;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderStatus;
import com.tk.cratemanagement.domain.enumeration.ShipmentOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ShipmentOrder entity.
 */
@Repository
public interface ShipmentOrderRepository extends JpaRepository<ShipmentOrder, Long> {
    
    Optional<ShipmentOrder> findByTenantIdAndOrderNumber(Long tenantId, String orderNumber);
    
    /**
     * 根据租户ID查找所有单据
     */
    List<ShipmentOrder> findByTenantId(Long tenantId);
    
    /**
     * 根据单据ID和租户ID查找单据
     */
    Optional<ShipmentOrder> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * 根据租户ID和单据类型查找单据
     */
    List<ShipmentOrder> findByTenantIdAndType(Long tenantId, ShipmentOrderType type);
    
    /**
     * 根据租户ID和单据状态查找单据
     */
    List<ShipmentOrder> findByTenantIdAndStatus(Long tenantId, ShipmentOrderStatus status);
    
    /**
     * 根据租户ID、单据类型和状态查找单据
     */
    List<ShipmentOrder> findByTenantIdAndTypeAndStatus(Long tenantId, ShipmentOrderType type, ShipmentOrderStatus status);
}
