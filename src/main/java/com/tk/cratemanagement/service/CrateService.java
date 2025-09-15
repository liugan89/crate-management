package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.*;

import java.util.List;

/**
 * 周转筐管理服务接口
 * 处理周转筐的注册、查询和管理操作
 */
public interface CrateService {

    /**
     * 注册新的周转筐
     *
     * @param request 周转筐注册请求
     * @param tenantId 租户ID
     * @return 周转筐DTO
     */
    CrateDTO registerCrate(CrateRequestDTO request, Long tenantId);

    /**
     * 批量注册周转筐
     * 支持一次性注册多个周转筐，提供详细的成功/失败信息
     *
     * @param request 批量注册请求
     * @param tenantId 租户ID
     * @return 批量注册响应，包含成功和失败的详细信息
     */
    BatchRegisterResponseDTO batchRegisterCrates(BatchRegisterCratesRequestDTO request, Long tenantId);

    /**
     * 获取所有周转筐列表
     *
     * @param tenantId 租户ID
     * @return 周转筐列表
     */
    List<CrateDTO> getAllCrates(Long tenantId);

    /**
     * 根据ID获取周转筐详情
     *
     * @param crateId 周转筐ID
     * @param tenantId 租户ID
     * @return 周转筐DTO
     */
    CrateDTO getCrateById(Long crateId, Long tenantId);

    /**
     * 更新周转筐信息
     *
     * @param crateId 周转筐ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的周转筐DTO
     */
    CrateDTO updateCrate(Long crateId, CrateRequestDTO request, Long tenantId);

    /**
     * 根据NFC UID查询周转筐详情
     * 高频操作：移动端扫码查询
     *
     * @param nfcUid NFC唯一标识
     * @param tenantId 租户ID
     * @return 周转筐详情（包含当前内容）
     */
    CrateDetailsDTO lookupCrateByNfcUid(String nfcUid, Long tenantId);

    /**
     * 创建周转筐类型
     *
     * @param request 周转筐类型请求
     * @param tenantId 租户ID
     * @return 周转筐类型DTO
     */
    CrateTypeDTO createCrateType(CrateTypeRequestDTO request, Long tenantId);

    /**
     * 获取所有周转筐类型
     *
     * @param tenantId 租户ID
     * @return 周转筐类型列表
     */
    List<CrateTypeDTO> getAllCrateTypes(Long tenantId);

    /**
     * 更新周转筐类型
     *
     * @param typeId 类型ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的周转筐类型DTO
     */
    CrateTypeDTO updateCrateType(Long typeId, CrateTypeRequestDTO request, Long tenantId);

    /**
     * 删除周转筐类型
     *
     * @param typeId 类型ID
     * @param tenantId 租户ID
     */
    void deleteCrateType(Long typeId, Long tenantId);

    /**
     * 报废周转筐
     * 将周转筐状态设置为INACTIVE，表示不再使用
     *
     * @param crateId 周转筐ID
     * @param tenantId 租户ID
     * @param reason 报废原因（可选）
     * @return 更新后的周转筐DTO
     */
    CrateDTO deactivateCrate(Long crateId, Long tenantId, String reason);

    /**
     * 批量报废周转筐
     * 将多个周转筐状态设置为INACTIVE
     *
     * @param crateIds 周转筐ID列表
     * @param tenantId 租户ID
     * @param reason 报废原因（可选）
     * @return 报废成功的周转筐数量
     */
    int batchDeactivateCrates(List<Long> crateIds, Long tenantId, String reason);
}