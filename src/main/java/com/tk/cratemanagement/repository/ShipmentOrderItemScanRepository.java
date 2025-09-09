package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.ShipmentOrderItemScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ShipmentOrderItemScan entity.
 */
@Repository
public interface ShipmentOrderItemScanRepository extends JpaRepository<ShipmentOrderItemScan, Long> {
    
    /**
     * 根据单据ID查找所有扫码记录
     */
    @Query("SELECT scan FROM ShipmentOrderItemScan scan WHERE scan.orderItem.shipmentOrder.id = :orderId")
    List<ShipmentOrderItemScan> findByOrderItemShipmentOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据行项ID和周转筐ID查找扫码记录
     */
    Optional<ShipmentOrderItemScan> findByOrderItemIdAndCrateId(Long orderItemId, Long crateId);
}
