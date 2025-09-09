package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.Goods;
import com.tk.cratemanagement.domain.Location;
import com.tk.cratemanagement.domain.Supplier;
import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.repository.GoodsRepository;
import com.tk.cratemanagement.repository.LocationRepository;
import com.tk.cratemanagement.repository.SupplierRepository;
import com.tk.cratemanagement.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 主数据管理服务实现类
 * 实现货物、供应商、库位等主数据的CRUD业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final GoodsRepository goodsRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;

    // ========== 货物管理实现 ==========

    @Override
    @Transactional
    public GoodsDTO createGoods(GoodsRequestDTO request, Long tenantId) {
        log.info("创建货物: name={}, sku={}, tenantId={}", request.name(), request.sku(), tenantId);

        // 检查SKU在租户内是否已存在
        if (request.sku() != null && goodsRepository.findByTenantIdAndSku(tenantId, request.sku()).isPresent()) {
            throw new IllegalArgumentException("SKU在当前租户内已存在: " + request.sku());
        }

        Goods goods = new Goods();
        goods.setTenantId(tenantId);
        goods.setName(request.name());
        goods.setSku(request.sku());

        goods = goodsRepository.save(goods);
        log.info("货物创建成功: goodsId={}", goods.getId());

        return convertToGoodsDTO(goods);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoodsDTO> getAllGoods(Long tenantId) {
        log.debug("获取租户货物列表: tenantId={}", tenantId);
        
        List<Goods> goodsList = goodsRepository.findByTenantId(tenantId);
        return goodsList.stream()
                .map(this::convertToGoodsDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GoodsDTO getGoodsById(Long goodsId, Long tenantId) {
        log.debug("获取货物详情: goodsId={}, tenantId={}", goodsId, tenantId);
        
        Goods goods = goodsRepository.findByIdAndTenantId(goodsId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("货物不存在"));
        
        return convertToGoodsDTO(goods);
    }

    @Override
    @Transactional
    public GoodsDTO updateGoods(Long goodsId, GoodsRequestDTO request, Long tenantId) {
        log.info("更新货物信息: goodsId={}, tenantId={}", goodsId, tenantId);
        
        Goods goods = goodsRepository.findByIdAndTenantId(goodsId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("货物不存在"));

        // 检查SKU冲突
        if (request.sku() != null && !request.sku().equals(goods.getSku())) {
            if (goodsRepository.findByTenantIdAndSku(tenantId, request.sku()).isPresent()) {
                throw new IllegalArgumentException("SKU在当前租户内已存在: " + request.sku());
            }
        }

        goods.setName(request.name());
        goods.setSku(request.sku());

        goods = goodsRepository.save(goods);
        log.info("货物信息更新成功: goodsId={}", goods.getId());

        return convertToGoodsDTO(goods);
    }

    @Override
    @Transactional
    public void deleteGoods(Long goodsId, Long tenantId) {
        log.info("删除货物: goodsId={}, tenantId={}", goodsId, tenantId);
        
        Goods goods = goodsRepository.findByIdAndTenantId(goodsId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("货物不存在"));

        goodsRepository.delete(goods);
        log.info("货物删除成功: goodsId={}", goodsId);
    }

    // ========== 供应商管理实现 ==========

    @Override
    @Transactional
    public SupplierDTO createSupplier(SupplierRequestDTO request, Long tenantId) {
        log.info("创建供应商: name={}, tenantId={}", request.name(), tenantId);

        Supplier supplier = new Supplier();
        supplier.setTenantId(tenantId);
        supplier.setName(request.name());

        supplier = supplierRepository.save(supplier);
        log.info("供应商创建成功: supplierId={}", supplier.getId());

        return convertToSupplierDTO(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers(Long tenantId) {
        log.debug("获取租户供应商列表: tenantId={}", tenantId);
        
        List<Supplier> suppliers = supplierRepository.findByTenantId(tenantId);
        return suppliers.stream()
                .map(this::convertToSupplierDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long supplierId, Long tenantId) {
        log.debug("获取供应商详情: supplierId={}, tenantId={}", supplierId, tenantId);
        
        Supplier supplier = supplierRepository.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));
        
        return convertToSupplierDTO(supplier);
    }

    @Override
    @Transactional
    public SupplierDTO updateSupplier(Long supplierId, SupplierRequestDTO request, Long tenantId) {
        log.info("更新供应商信息: supplierId={}, tenantId={}", supplierId, tenantId);
        
        Supplier supplier = supplierRepository.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));

        supplier.setName(request.name());

        supplier = supplierRepository.save(supplier);
        log.info("供应商信息更新成功: supplierId={}", supplier.getId());

        return convertToSupplierDTO(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long supplierId, Long tenantId) {
        log.info("删除供应商: supplierId={}, tenantId={}", supplierId, tenantId);
        
        Supplier supplier = supplierRepository.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));

        supplierRepository.delete(supplier);
        log.info("供应商删除成功: supplierId={}", supplierId);
    }

    // ========== 库位管理实现 ==========

    @Override
    @Transactional
    public LocationDTO createLocation(LocationRequestDTO request, Long tenantId) {
        log.info("创建库位: name={}, tenantId={}", request.name(), tenantId);

        Location location = new Location();
        location.setTenantId(tenantId);
        location.setName(request.name());

        location = locationRepository.save(location);
        log.info("库位创建成功: locationId={}", location.getId());

        return convertToLocationDTO(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO> getAllLocations(Long tenantId) {
        log.debug("获取租户库位列表: tenantId={}", tenantId);
        
        List<Location> locations = locationRepository.findByTenantId(tenantId);
        return locations.stream()
                .map(this::convertToLocationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDTO getLocationById(Long locationId, Long tenantId) {
        log.debug("获取库位详情: locationId={}, tenantId={}", locationId, tenantId);
        
        Location location = locationRepository.findByIdAndTenantId(locationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));
        
        return convertToLocationDTO(location);
    }

    @Override
    @Transactional
    public LocationDTO updateLocation(Long locationId, LocationRequestDTO request, Long tenantId) {
        log.info("更新库位信息: locationId={}, tenantId={}", locationId, tenantId);
        
        Location location = locationRepository.findByIdAndTenantId(locationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));

        location.setName(request.name());

        location = locationRepository.save(location);
        log.info("库位信息更新成功: locationId={}", location.getId());

        return convertToLocationDTO(location);
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId, Long tenantId) {
        log.info("删除库位: locationId={}, tenantId={}", locationId, tenantId);
        
        Location location = locationRepository.findByIdAndTenantId(locationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));

        locationRepository.delete(location);
        log.info("库位删除成功: locationId={}", locationId);
    }

    // ========== DTO转换方法 ==========

    private GoodsDTO convertToGoodsDTO(Goods goods) {
        return new GoodsDTO(
                goods.getId(),
                goods.getName(),
                goods.getSku(),
                null // description字段暂时设为null，需要在Goods实体中添加
        );
    }

    private SupplierDTO convertToSupplierDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                null, // contactPerson字段暂时设为null
                null, // contactPhone字段暂时设为null
                null  // address字段暂时设为null
        );
    }

    private LocationDTO convertToLocationDTO(Location location) {
        return new LocationDTO(
                location.getId(),
                location.getName(),
                null // description字段暂时设为null
        );
    }
}