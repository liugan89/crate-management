package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 主数据管理控制器
 * 处理货物、供应商、库位等主数据的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "主数据管理", description = "货物、供应商、库位等主数据管理相关API")
public class MasterDataController {

    private final MasterDataService masterDataService;

    // ========== 货物管理 ==========

    /**
     * 创建货物
     */
    @PostMapping("/goods")
    @Operation(summary = "创建货物", description = "创建新的货物主数据")
    public ResponseEntity<GoodsDTO> createGoods(@Valid @RequestBody GoodsRequestDTO request,
                                              Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到创建货物请求: name={}, sku={}, tenantId={}", request.name(), request.sku(), tenantId);
        
        GoodsDTO response = masterDataService.createGoods(request, tenantId);
        
        log.info("货物创建成功: goodsId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取所有货物
     */
    @GetMapping("/goods")
    @Operation(summary = "获取货物列表", description = "获取当前租户的所有货物")
    public ResponseEntity<List<GoodsDTO>> getAllGoods(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取货物列表: tenantId={}", tenantId);
        
        List<GoodsDTO> goods = masterDataService.getAllGoods(tenantId);
        
        return ResponseEntity.ok(goods);
    }

    /**
     * 获取货物详情
     */
    @GetMapping("/goods/{id}")
    @Operation(summary = "获取货物详情", description = "获取指定货物的详细信息")
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable Long id,
                                               Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取货物详情: goodsId={}, tenantId={}", id, tenantId);
        
        GoodsDTO goods = masterDataService.getGoodsById(id, tenantId);
        
        return ResponseEntity.ok(goods);
    }

    /**
     * 更新货物信息
     */
    @PutMapping("/goods/{id}")
    @Operation(summary = "更新货物信息", description = "更新货物的基本信息")
    public ResponseEntity<GoodsDTO> updateGoods(@PathVariable Long id,
                                              @Valid @RequestBody GoodsRequestDTO request,
                                              Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到更新货物请求: goodsId={}, tenantId={}", id, tenantId);
        
        GoodsDTO response = masterDataService.updateGoods(id, request, tenantId);
        
        log.info("货物信息更新成功: goodsId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除货物
     */
    @DeleteMapping("/goods/{id}")
    @Operation(summary = "删除货物", description = "删除指定的货物")
    public ResponseEntity<Void> deleteGoods(@PathVariable Long id,
                                          Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到删除货物请求: goodsId={}, tenantId={}", id, tenantId);
        
        masterDataService.deleteGoods(id, tenantId);
        
        log.info("货物删除成功: goodsId={}", id);
        return ResponseEntity.noContent().build();
    }

    // ========== 供应商管理 ==========

    /**
     * 创建供应商
     */
    @PostMapping("/suppliers")
    @Operation(summary = "创建供应商", description = "创建新的供应商主数据")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到创建供应商请求: name={}, tenantId={}", request.name(), tenantId);
        
        SupplierDTO response = masterDataService.createSupplier(request, tenantId);
        
        log.info("供应商创建成功: supplierId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取所有供应商
     */
    @GetMapping("/suppliers")
    @Operation(summary = "获取供应商列表", description = "获取当前租户的所有供应商")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取供应商列表: tenantId={}", tenantId);
        
        List<SupplierDTO> suppliers = masterDataService.getAllSuppliers(tenantId);
        
        return ResponseEntity.ok(suppliers);
    }

    /**
     * 获取供应商详情
     */
    @GetMapping("/suppliers/{id}")
    @Operation(summary = "获取供应商详情", description = "获取指定供应商的详细信息")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id,
                                                      Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取供应商详情: supplierId={}, tenantId={}", id, tenantId);
        
        SupplierDTO supplier = masterDataService.getSupplierById(id, tenantId);
        
        return ResponseEntity.ok(supplier);
    }

    /**
     * 更新供应商信息
     */
    @PutMapping("/suppliers/{id}")
    @Operation(summary = "更新供应商信息", description = "更新供应商的基本信息")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id,
                                                     @Valid @RequestBody SupplierRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到更新供应商请求: supplierId={}, tenantId={}", id, tenantId);
        
        SupplierDTO response = masterDataService.updateSupplier(id, request, tenantId);
        
        log.info("供应商信息更新成功: supplierId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/suppliers/{id}")
    @Operation(summary = "删除供应商", description = "删除指定的供应商")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id,
                                             Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到删除供应商请求: supplierId={}, tenantId={}", id, tenantId);
        
        masterDataService.deleteSupplier(id, tenantId);
        
        log.info("供应商删除成功: supplierId={}", id);
        return ResponseEntity.noContent().build();
    }

    // ========== 库位管理 ==========

    /**
     * 创建库位
     */
    @PostMapping("/locations")
    @Operation(summary = "创建库位", description = "创建新的库位主数据")
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到创建库位请求: name={}, tenantId={}", request.name(), tenantId);
        
        LocationDTO response = masterDataService.createLocation(request, tenantId);
        
        log.info("库位创建成功: locationId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取所有库位
     */
    @GetMapping("/locations")
    @Operation(summary = "获取库位列表", description = "获取当前租户的所有库位")
    public ResponseEntity<List<LocationDTO>> getAllLocations(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取库位列表: tenantId={}", tenantId);
        
        List<LocationDTO> locations = masterDataService.getAllLocations(tenantId);
        
        return ResponseEntity.ok(locations);
    }

    /**
     * 获取库位详情
     */
    @GetMapping("/locations/{id}")
    @Operation(summary = "获取库位详情", description = "获取指定库位的详细信息")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id,
                                                      Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.debug("获取库位详情: locationId={}, tenantId={}", id, tenantId);
        
        LocationDTO location = masterDataService.getLocationById(id, tenantId);
        
        return ResponseEntity.ok(location);
    }

    /**
     * 更新库位信息
     */
    @PutMapping("/locations/{id}")
    @Operation(summary = "更新库位信息", description = "更新库位的基本信息")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id,
                                                     @Valid @RequestBody LocationRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到更新库位请求: locationId={}, tenantId={}", id, tenantId);
        
        LocationDTO response = masterDataService.updateLocation(id, request, tenantId);
        
        log.info("库位信息更新成功: locationId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除库位
     */
    @DeleteMapping("/locations/{id}")
    @Operation(summary = "删除库位", description = "删除指定的库位")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id,
                                             Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("收到删除库位请求: locationId={}, tenantId={}", id, tenantId);
        
        masterDataService.deleteLocation(id, tenantId);
        
        log.info("库位删除成功: locationId={}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 从认证信息中提取租户ID
     */
    private Long getTenantIdFromAuth(Authentication authentication) {
        // TODO: 从JWT token中提取租户ID
        return 1L; // 临时返回
    }
}