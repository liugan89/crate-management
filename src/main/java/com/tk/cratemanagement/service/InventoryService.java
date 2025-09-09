package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.InventoryDetailDTO;
import com.tk.cratemanagement.dto.InventorySummaryDTO;
import com.tk.cratemanagement.dto.OperationLogDTO;

import java.util.List;

/**
 * 库存与历史服务接口
 * 处理实时库存查询和操作历史追溯
 */
public interface InventoryService {

    /**
     * 获取实时库存汇总
     * 按货物分组的库存总览
     *
     * @param tenantId 租户ID
     * @return 库存汇总列表
     */
    List<InventorySummaryDTO> getInventorySummary(Long tenantId);

    /**
     * 获取指定货物的详细库存
     * 显示所有包含该货物的周转筐详情
     *
     * @param goodsId 货物ID
     * @param tenantId 租户ID
     * @return 库存详情列表
     */
    List<InventoryDetailDTO> getInventoryDetails(Long goodsId, Long tenantId);

    /**
     * 获取周转筐的操作历史
     * 追溯单个周转筐的完整生命周期
     *
     * @param nfcUid 周转筐NFC UID
     * @param tenantId 租户ID
     * @return 操作日志列表
     */
    List<OperationLogDTO> getCrateHistory(String nfcUid, Long tenantId);
}