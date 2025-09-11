package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.Crate;
import com.tk.cratemanagement.domain.CrateContent;
import com.tk.cratemanagement.domain.CrateType;
import com.tk.cratemanagement.domain.enumeration.CrateStatus;
import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.repository.CrateContentRepository;
import com.tk.cratemanagement.repository.CrateRepository;
import com.tk.cratemanagement.repository.CrateTypeRepository;
import com.tk.cratemanagement.service.CrateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 周转筐管理服务实现类
 * 实现周转筐的注册、查询和管理业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrateServiceImpl implements CrateService {

    private final CrateRepository crateRepository;
    private final CrateTypeRepository crateTypeRepository;
    private final CrateContentRepository crateContentRepository;

    @Override
    @Transactional
    public CrateDTO registerCrate(CrateRequestDTO request, Long tenantId) {
        log.info("注册新周转筐: nfcUid={}, tenantId={}", request.nfcUid(), tenantId);

        // 检查NFC UID在租户内是否已存在
        if (crateRepository.findByTenantIdAndNfcUid(tenantId, request.nfcUid()).isPresent()) {
            throw new IllegalArgumentException("NFC UID在当前租户内已存在: " + request.nfcUid());
        }

        Crate crate = new Crate();
        crate.setTenantId(tenantId);
        crate.setNfcUid(request.nfcUid());
        crate.setStatus(CrateStatus.AVAILABLE);

        // 设置周转筐类型
        if (request.crateTypeId() != null) {
            CrateType crateType = crateTypeRepository.findByIdAndTenantId(request.crateTypeId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("周转筐类型不存在"));
            crate.setCrateType(crateType);
        }

        crate = crateRepository.save(crate);
        log.info("周转筐注册成功: crateId={}, nfcUid={}", crate.getId(), crate.getNfcUid());

        return convertToDTO(crate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrateDTO> getAllCrates(Long tenantId) {
        log.debug("获取租户周转筐列表: tenantId={}", tenantId);
        
        List<Crate> crates = crateRepository.findByTenantId(tenantId);
        return crates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CrateDTO getCrateById(Long crateId, Long tenantId) {
        log.debug("获取周转筐详情: crateId={}, tenantId={}", crateId, tenantId);
        
        Crate crate = crateRepository.findByIdAndTenantId(crateId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在"));
        
        return convertToDTO(crate);
    }

    @Override
    @Transactional
    public CrateDTO updateCrate(Long crateId, CrateRequestDTO request, Long tenantId) {
        log.info("更新周转筐信息: crateId={}, tenantId={}", crateId, tenantId);
        
        Crate crate = crateRepository.findByIdAndTenantId(crateId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在"));

        // 检查NFC UID是否与其他周转筐冲突
        if (!crate.getNfcUid().equals(request.nfcUid())) {
            if (crateRepository.findByTenantIdAndNfcUid(tenantId, request.nfcUid()).isPresent()) {
                throw new IllegalArgumentException("NFC UID在当前租户内已存在: " + request.nfcUid());
            }
            crate.setNfcUid(request.nfcUid());
        }

        // 更新周转筐类型
        if (request.crateTypeId() != null) {
            CrateType crateType = crateTypeRepository.findByIdAndTenantId(request.crateTypeId(), tenantId)
                    .orElseThrow(() -> new IllegalArgumentException("周转筐类型不存在"));
            crate.setCrateType(crateType);
        } else {
            crate.setCrateType(null);
        }

        crate = crateRepository.save(crate);
        log.info("周转筐信息更新成功: crateId={}", crate.getId());

        return convertToDTO(crate);
    }

    @Override
    @Transactional(readOnly = true)
    public CrateDetailsDTO lookupCrateByNfcUid(String nfcUid, Long tenantId) {
        log.debug("根据NFC UID查询周转筐: nfcUid={}, tenantId={}", nfcUid, tenantId);
        
        Crate crate = crateRepository.findByTenantIdAndNfcUid(tenantId, nfcUid)
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在: " + nfcUid));

        CrateDTO crateInfo = convertToDTO(crate);
        
        // 获取当前内容
        CrateContentDTO currentContent = null;
        Optional<CrateContent> contentOpt = crateContentRepository.findByCrateId(crate.getId());
        if (contentOpt.isPresent()) {
            currentContent = convertToContentDTO(contentOpt.get());
        }

        return new CrateDetailsDTO(crateInfo, currentContent);
    }

    @Override
    @Transactional
    public CrateTypeDTO createCrateType(CrateTypeRequestDTO request, Long tenantId) {
        log.info("创建周转筐类型: name={}, tenantId={}", request.name(), tenantId);

        CrateType crateType = new CrateType();
        crateType.setTenantId(tenantId);
        crateType.setName(request.name());
        crateType.setCapacity(request.capacity() != null ? request.capacity() : null);
        crateType.setWeight(request.weight() != null ? request.weight() : null);

        crateType = crateTypeRepository.save(crateType);
        log.info("周转筐类型创建成功: typeId={}", crateType.getId());

        return convertToTypeDTO(crateType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrateTypeDTO> getAllCrateTypes(Long tenantId) {
        log.debug("获取租户周转筐类型列表: tenantId={}", tenantId);
        
        List<CrateType> crateTypes = crateTypeRepository.findByTenantId(tenantId);
        return crateTypes.stream()
                .map(this::convertToTypeDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CrateTypeDTO updateCrateType(Long typeId, CrateTypeRequestDTO request, Long tenantId) {
        log.info("更新周转筐类型: typeId={}, tenantId={}", typeId, tenantId);
        
        CrateType crateType = crateTypeRepository.findByIdAndTenantId(typeId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("周转筐类型不存在"));

        crateType.setName(request.name());
        crateType.setCapacity(request.capacity() != null ? request.capacity() : null);
        crateType.setWeight(request.weight() != null ? request.weight() : null);

        crateType = crateTypeRepository.save(crateType);
        log.info("周转筐类型更新成功: typeId={}", crateType.getId());

        return convertToTypeDTO(crateType);
    }

    @Override
    @Transactional
    public void deleteCrateType(Long typeId, Long tenantId) {
        log.info("删除周转筐类型: typeId={}, tenantId={}", typeId, tenantId);
        
        CrateType crateType = crateTypeRepository.findByIdAndTenantId(typeId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("周转筐类型不存在"));

        // 检查是否有周转筐正在使用此类型
        if (crateRepository.existsByCrateTypeId(typeId)) {
            throw new IllegalStateException("无法删除正在使用的周转筐类型");
        }

        crateTypeRepository.delete(crateType);
        log.info("周转筐类型删除成功: typeId={}", typeId);
    }

    // DTO转换方法
    private CrateDTO convertToDTO(Crate crate) {
        return new CrateDTO(
                crate.getId(),
                crate.getNfcUid(),
                crate.getStatus(),
                crate.getCrateType() != null ? crate.getCrateType().getId() : null,
                crate.getCrateType() != null ? crate.getCrateType().getName() : null,
                crate.getLastKnownLocation() != null ? crate.getLastKnownLocation().getId() : null,
                crate.getLastKnownLocation() != null ? crate.getLastKnownLocation().getName() : null,
                crate.getLastSeenAt(),
                crate.getMaintenanceDueDate(),
                crate.getCreatedAt(),
                crate.getUpdatedAt()
        );
    }

    private CrateTypeDTO convertToTypeDTO(CrateType crateType) {
        return new CrateTypeDTO(
                crateType.getId(),
                crateType.getTenantId(),
                crateType.getName(),
                crateType.getCapacity(),
                crateType.getWeight(),
                crateType.getDimensions(),
                crateType.getMaterial(),
                crateType.getColor(),
                crateType.getIsActive(),
                crateType.getCreatedAt(),
                crateType.getUpdatedAt()
        );
    }

    private CrateContentDTO convertToContentDTO(CrateContent content) {
        return new CrateContentDTO(
                content.getId(),
                content.getGoods() != null ? content.getGoods().getId() : null,
                content.getGoods() != null ? content.getGoods().getName() : null,
                content.getQuantity() != null ? content.getQuantity().doubleValue() : null,
                content.getStatus(),
                content.getLocation() != null ? content.getLocation().getId() : null,
                content.getLocation() != null ? content.getLocation().getName() : null,
                content.getLastUpdatedAt()
        );
    }
}