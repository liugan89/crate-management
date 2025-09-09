package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.*;

import java.util.List;

/**
 * 仓储操作服务接口
 * 处理核心仓储业务逻辑：入库、出库、调整等操作
 */
public interface WarehouseService {

    /**
     * 创建出入库单据
     *
     * @param request 创建单据请求
     * @param tenantId 租户ID
     * @return 单据DTO
     */
    ShipmentOrderDTO createShipmentOrder(CreateShipmentOrderRequestDTO request, Long tenantId);

    /**
     * 获取单据列表
     *
     * @param tenantId 租户ID
     * @param type 单据类型（可选）
     * @param status 单据状态（可选）
     * @return 单据摘要列表
     */
    List<ShipmentOrderSummaryDTO> getShipmentOrders(Long tenantId, String type, String status);

    /**
     * 获取单据详情
     *
     * @param orderId 单据ID
     * @param tenantId 租户ID
     * @return 单据详情DTO
     */
    ShipmentOrderDetailsDTO getShipmentOrderDetails(Long orderId, Long tenantId);

    /**
     * 完成单据
     * 核心业务逻辑：原子性操作，触发库存更新
     *
     * @param orderId 单据ID
     * @param tenantId 租户ID
     * @return 完成后的单据详情
     */
    ShipmentOrderDetailsDTO completeShipmentOrder(Long orderId, Long tenantId);

    /**
     * 为单据添加行项
     *
     * @param orderId 单据ID
     * @param request 创建行项请求
     * @param tenantId 租户ID
     * @return 行项DTO
     */
    OrderItemDTO addOrderItem(Long orderId, CreateOrderItemRequestDTO request, Long tenantId);

    /**
     * 为行项添加扫码记录
     * 高频操作：记录NFC扫描和实际数量
     *
     * @param itemId 行项ID
     * @param request 扫码请求
     * @param tenantId 租户ID
     * @param userId 操作用户ID
     * @return 扫码记录DTO
     */
    ScanDTO addScan(Long itemId, CreateScanRequestDTO request, Long tenantId, Long userId);
}