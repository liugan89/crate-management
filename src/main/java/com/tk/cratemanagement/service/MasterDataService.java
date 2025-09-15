package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 主数据管理服务接口
 * 处理货物、供应商、库位等主数据的CRUD操作
 */
public interface MasterDataService {

    // ========== 货物管理 ==========
    
    /**
     * 创建货物
     *
     * @param request 货物创建请求
     * @param tenantId 租户ID
     * @return 货物DTO
     */
    GoodsDTO createGoods(GoodsRequestDTO request, Long tenantId);

    /**
     * 获取所有货物
     *
     * @param tenantId 租户ID
     * @return 货物列表
     */
    List<GoodsDTO> getAllGoods(Long tenantId);

    /**
     * 根据查询条件获取货物列表
     *
     * @param tenantId 租户ID
     * @param name 名称模糊查询（可选）
     * @param isActive 激活状态过滤（可选）
     * @return 货物列表
     */
    List<GoodsDTO> getGoodsWithFilters(Long tenantId, String name, Boolean isActive);

    /**
     * 根据ID获取货物
     *
     * @param goodsId 货物ID
     * @param tenantId 租户ID
     * @return 货物DTO
     */
    GoodsDTO getGoodsById(Long goodsId, Long tenantId);

    /**
     * 更新货物
     *
     * @param goodsId 货物ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的货物DTO
     */
    GoodsDTO updateGoods(Long goodsId, GoodsRequestDTO request, Long tenantId);

    /**
     * 删除货物（软删除）
     *
     * @param goodsId 货物ID
     * @param tenantId 租户ID
     */
    void deleteGoods(Long goodsId, Long tenantId);

    /**
     * 上传商品图片
     *
     * @param file 图片文件
     * @param goodsId 商品ID
     * @param tenantId 租户ID
     * @return 图片URL
     */
    String uploadGoodsImage(MultipartFile file, Long goodsId, Long tenantId);

    // ========== 供应商管理 ==========
    
    /**
     * 创建供应商
     *
     * @param request 供应商创建请求
     * @param tenantId 租户ID
     * @return 供应商DTO
     */
    SupplierDTO createSupplier(SupplierRequestDTO request, Long tenantId);

    /**
     * 获取所有供应商
     *
     * @param tenantId 租户ID
     * @return 供应商列表
     */
    List<SupplierDTO> getAllSuppliers(Long tenantId);

    /**
     * 根据查询条件获取供应商列表
     *
     * @param tenantId 租户ID
     * @param name 名称模糊查询（可选）
     * @param isActive 激活状态过滤（可选）
     * @return 供应商列表
     */
    List<SupplierDTO> getSuppliersWithFilters(Long tenantId, String name, Boolean isActive);

    /**
     * 根据ID获取供应商
     *
     * @param supplierId 供应商ID
     * @param tenantId 租户ID
     * @return 供应商DTO
     */
    SupplierDTO getSupplierById(Long supplierId, Long tenantId);

    /**
     * 更新供应商
     *
     * @param supplierId 供应商ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的供应商DTO
     */
    SupplierDTO updateSupplier(Long supplierId, SupplierRequestDTO request, Long tenantId);

    /**
     * 删除供应商
     *
     * @param supplierId 供应商ID
     * @param tenantId 租户ID
     */
    void deleteSupplier(Long supplierId, Long tenantId);

    // ========== 库位管理 ==========
    
    /**
     * 创建库位
     *
     * @param request 库位创建请求
     * @param tenantId 租户ID
     * @return 库位DTO
     */
    LocationDTO createLocation(LocationRequestDTO request, Long tenantId);

    /**
     * 获取所有库位
     *
     * @param tenantId 租户ID
     * @return 库位列表
     */
    List<LocationDTO> getAllLocations(Long tenantId);

    /**
     * 根据查询条件获取库位列表
     *
     * @param tenantId 租户ID
     * @param name 名称模糊查询（可选）
     * @param isActive 激活状态过滤（可选）
     * @return 库位列表
     */
    List<LocationDTO> getLocationsWithFilters(Long tenantId, String name, Boolean isActive);

    /**
     * 根据ID获取库位
     *
     * @param locationId 库位ID
     * @param tenantId 租户ID
     * @return 库位DTO
     */
    LocationDTO getLocationById(Long locationId, Long tenantId);

    /**
     * 更新库位
     *
     * @param locationId 库位ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的库位DTO
     */
    LocationDTO updateLocation(Long locationId, LocationRequestDTO request, Long tenantId);

    /**
     * 删除库位
     *
     * @param locationId 库位ID
     * @param tenantId 租户ID
     */
    void deleteLocation(Long locationId, Long tenantId);
}