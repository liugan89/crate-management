package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.Goods;
import com.tk.cratemanagement.domain.Location;
import com.tk.cratemanagement.domain.Supplier;
import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.repository.GoodsRepository;
import com.tk.cratemanagement.repository.LocationRepository;
import com.tk.cratemanagement.repository.SupplierRepository;
import com.tk.cratemanagement.service.MasterDataService;
import com.tk.cratemanagement.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
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
    private final FileUploadUtil fileUploadUtil;

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
        goods.setBarcode(request.barcode());
        goods.setUnit(request.unit());
        goods.setCategory(request.category());
        goods.setImageUrl(request.imageUrl());
        goods.setDescription(request.description());
        goods.setCustomFields(request.customFields());
        goods.setIsActive(request.isActive() != null ? request.isActive() : true);
        goods.setCreatedAt(Instant.now());
        goods.setUpdatedAt(Instant.now());

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

        // 如果图片URL发生变化，删除旧图片
        if (request.imageUrl() != null && !request.imageUrl().equals(goods.getImageUrl())) {
            if (goods.getImageUrl() != null) {
                fileUploadUtil.deleteGoodsImage(goods.getImageUrl(), tenantId);
            }
        }

        goods.setName(request.name());
        goods.setSku(request.sku());
        goods.setBarcode(request.barcode());
        goods.setUnit(request.unit());
        goods.setCategory(request.category());
        goods.setImageUrl(request.imageUrl());
        goods.setDescription(request.description());
        goods.setCustomFields(request.customFields());
        if (request.isActive() != null) {
            goods.setIsActive(request.isActive());
        }
        goods.setUpdatedAt(Instant.now());

        goods = goodsRepository.save(goods);
        log.info("货物信息更新成功: goodsId={}", goods.getId());

        return convertToGoodsDTO(goods);
    }

    @Override
    @Transactional
    public void deleteGoods(Long goodsId, Long tenantId) {
        log.info("软删除货物: goodsId={}, tenantId={}", goodsId, tenantId);
        
        Goods goods = goodsRepository.findByIdAndTenantId(goodsId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("货物不存在"));

        // 软删除：设置deleted_at时间戳
        goods.setDeletedAt(Instant.now());
        goods.setUpdatedAt(Instant.now());
        
        // 删除关联的图片文件
        if (goods.getImageUrl() != null) {
            fileUploadUtil.deleteGoodsImage(goods.getImageUrl(), tenantId);
        }
        
        goodsRepository.save(goods);
        log.info("货物软删除成功: goodsId={}", goodsId);
    }

    @Override
    @Transactional
    public String uploadGoodsImage(MultipartFile file, Long goodsId, Long tenantId) {
        log.info("上传商品图片: goodsId={}, tenantId={}, fileName={}", goodsId, tenantId, file.getOriginalFilename());
        
        // 验证商品是否存在
        Goods goods = goodsRepository.findByIdAndTenantId(goodsId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        
        try {
            // 上传图片文件
            String imageUrl = fileUploadUtil.uploadGoodsImage(file, tenantId, goodsId);
            
            // 如果商品已有图片，删除旧图片
            if (goods.getImageUrl() != null) {
                fileUploadUtil.deleteGoodsImage(goods.getImageUrl(), tenantId);
            }
            
            // 更新商品的图片URL
            goods.setImageUrl(imageUrl);
            goods.setUpdatedAt(Instant.now());
            goodsRepository.save(goods);
            
            log.info("商品图片上传成功: goodsId={}, imageUrl={}", goodsId, imageUrl);
            return imageUrl;
            
        } catch (IOException e) {
            log.error("商品图片上传失败: goodsId={}, tenantId={}", goodsId, tenantId, e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }
    }

    // ========== 供应商管理实现 ==========

    @Override
    @Transactional
    public SupplierDTO createSupplier(SupplierRequestDTO request, Long tenantId) {
        log.info("创建供应商: name={}, tenantId={}", request.name(), tenantId);

        // 检查供应商名称在租户内是否已存在
        if (supplierRepository.findByTenantIdAndName(tenantId, request.name()).isPresent()) {
            throw new IllegalArgumentException("供应商名称在当前租户内已存在: " + request.name());
        }

        // 检查供应商编码在租户内是否已存在
        if (request.code() != null && supplierRepository.findByTenantIdAndCode(tenantId, request.code()).isPresent()) {
            throw new IllegalArgumentException("供应商编码在当前租户内已存在: " + request.code());
        }

        Supplier supplier = new Supplier();
        supplier.setTenantId(tenantId);
        supplier.setName(request.name());
        supplier.setCode(request.code());
        supplier.setContactName(request.contactName());
        supplier.setContactEmail(request.contactEmail());
        supplier.setContactPhone(request.contactPhone());
        supplier.setAddress(request.address());
        supplier.setCity(request.city());
        supplier.setState(request.state());
        supplier.setZipCode(request.zipCode());
        supplier.setCountry(request.country() != null ? request.country() : "CN");
        supplier.setIsActive(request.isActive() != null ? request.isActive() : true);

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

        // 检查名称冲突
        if (!request.name().equals(supplier.getName())) {
            if (supplierRepository.findByTenantIdAndName(tenantId, request.name()).isPresent()) {
                throw new IllegalArgumentException("供应商名称在当前租户内已存在: " + request.name());
            }
        }

        // 检查编码冲突
        if (request.code() != null && !request.code().equals(supplier.getCode())) {
            if (supplierRepository.findByTenantIdAndCode(tenantId, request.code()).isPresent()) {
                throw new IllegalArgumentException("供应商编码在当前租户内已存在: " + request.code());
            }
        }

        supplier.setName(request.name());
        supplier.setCode(request.code());
        supplier.setContactName(request.contactName());
        supplier.setContactEmail(request.contactEmail());
        supplier.setContactPhone(request.contactPhone());
        supplier.setAddress(request.address());
        supplier.setCity(request.city());
        supplier.setState(request.state());
        supplier.setZipCode(request.zipCode());
        supplier.setCountry(request.country() != null ? request.country() : "CN");
        if (request.isActive() != null) {
            supplier.setIsActive(request.isActive());
        }

        supplier = supplierRepository.save(supplier);
        log.info("供应商信息更新成功: supplierId={}", supplier.getId());

        return convertToSupplierDTO(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long supplierId, Long tenantId) {
        log.info("软删除供应商: supplierId={}, tenantId={}", supplierId, tenantId);
        
        Supplier supplier = supplierRepository.findByIdAndTenantId(supplierId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));

        // 软删除：设置deleted_at时间戳
        supplier.setDeletedAt(java.time.LocalDateTime.now());
        supplier.setUpdatedAt(java.time.LocalDateTime.now());
        supplierRepository.save(supplier);
        
        log.info("供应商软删除成功: supplierId={}", supplierId);
    }

    // ========== 库位管理实现 ==========

    @Override
    @Transactional
    public LocationDTO createLocation(LocationRequestDTO request, Long tenantId) {
        log.info("创建库位: name={}, tenantId={}", request.name(), tenantId);

        // 检查库位名称在租户内是否已存在
        if (locationRepository.findByTenantIdAndName(tenantId, request.name()).isPresent()) {
            throw new IllegalArgumentException("库位名称在当前租户内已存在: " + request.name());
        }

        // 检查库位编码在租户内是否已存在
        if (request.code() != null && locationRepository.findByTenantIdAndCode(tenantId, request.code()).isPresent()) {
            throw new IllegalArgumentException("库位编码在当前租户内已存在: " + request.code());
        }

        Location location = new Location();
        location.setTenantId(tenantId);
        location.setName(request.name());
        location.setCode(request.code());
        location.setDescription(request.description());
        location.setZone(request.zone());
        location.setIsActive(request.isActive() != null ? request.isActive() : true);

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

        // 检查名称冲突
        if (!request.name().equals(location.getName())) {
            if (locationRepository.findByTenantIdAndName(tenantId, request.name()).isPresent()) {
                throw new IllegalArgumentException("库位名称在当前租户内已存在: " + request.name());
            }
        }

        // 检查编码冲突
        if (request.code() != null && !request.code().equals(location.getCode())) {
            if (locationRepository.findByTenantIdAndCode(tenantId, request.code()).isPresent()) {
                throw new IllegalArgumentException("库位编码在当前租户内已存在: " + request.code());
            }
        }

        location.setName(request.name());
        location.setCode(request.code());
        location.setDescription(request.description());
        location.setZone(request.zone());
        if (request.isActive() != null) {
            location.setIsActive(request.isActive());
        }

        location = locationRepository.save(location);
        log.info("库位信息更新成功: locationId={}", location.getId());

        return convertToLocationDTO(location);
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId, Long tenantId) {
        log.info("软删除库位: locationId={}, tenantId={}", locationId, tenantId);
        
        Location location = locationRepository.findByIdAndTenantId(locationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));

        // 软删除：设置deleted_at时间戳
        location.setDeletedAt(java.time.LocalDateTime.now());
        location.setUpdatedAt(java.time.LocalDateTime.now());
        locationRepository.save(location);
        
        log.info("库位软删除成功: locationId={}", locationId);
    }

    // ========== DTO转换方法 ==========

    private GoodsDTO convertToGoodsDTO(Goods goods) {
        return new GoodsDTO(
                goods.getId(),
                goods.getName(),
                goods.getSku(),
                goods.getBarcode(),
                goods.getUnit(),
                goods.getCategory(),
                goods.getImageUrl(),
                goods.getDescription(),
                goods.getCustomFields(),
                goods.getIsActive(),
                goods.getCreatedAt() != null ? goods.getCreatedAt().atZone(ZoneId.systemDefault()) : null,
                goods.getUpdatedAt() != null ? goods.getUpdatedAt().atZone(ZoneId.systemDefault()) : null
        );
    }

    private SupplierDTO convertToSupplierDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getCode(),
                supplier.getContactName(),
                supplier.getContactEmail(),
                supplier.getContactPhone(),
                supplier.getAddress(),
                supplier.getCity(),
                supplier.getState(),
                supplier.getZipCode(),
                supplier.getCountry(),
                supplier.getIsActive(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }

    private LocationDTO convertToLocationDTO(Location location) {
        return new LocationDTO(
                location.getId(),
                location.getName(),
                location.getCode(),
                location.getDescription(),
                location.getZone(),
                location.getIsActive(),
                location.getCreatedAt(),
                location.getUpdatedAt()
        );
    }
}