package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.ShipmentOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ShipmentOrderItem entity.
 */
@Repository
public interface ShipmentOrderItemRepository extends JpaRepository<ShipmentOrderItem, Long> {
    
    /**
     * 根据行项ID和租户ID查找行项（通过关联的单据验证租户）
     */
    @Query("SELECT soi FROM ShipmentOrderItem soi WHERE soi.id = :itemId AND soi.shipmentOrder.tenantId = :tenantId")
    Optional<ShipmentOrderItem> findByIdAndShipmentOrderTenantId(@Param("itemId") Long itemId, @Param("tenantId") Long tenantId);
    
    /**
     * 根据单据ID查找所有行项
     */
    List<ShipmentOrderItem> findByShipmentOrderId(Long shipmentOrderId);
    
    /**
     * 根据单据ID和租户ID查找所有行项
     */
    @Query("SELECT soi FROM ShipmentOrderItem soi WHERE soi.shipmentOrder.id = :orderId AND soi.shipmentOrder.tenantId = :tenantId")
    List<ShipmentOrderItem> findByShipmentOrderIdAndTenantId(@Param("orderId") Long orderId, @Param("tenantId") Long tenantId);
}
