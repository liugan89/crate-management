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

import java.util.*;
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
    @Transactional
    public BatchRegisterResponseDTO batchRegisterCrates(BatchRegisterCratesRequestDTO request, Long tenantId) {
        log.info("批量注册周转筐: 数量={}, tenantId={}, notes={}", 
                request.crates().size(), tenantId, request.notes());
        
        List<CrateDTO> successfulCrates = new ArrayList<>();
        List<BatchRegisterResponseDTO.BatchRegisterFailureDTO> failures = new ArrayList<>();
        
        // 预检查：检查所有NFC UID是否在租户内重复
        Set<String> requestNfcUids = request.crates().stream()
                .map(CrateRequestDTO::nfcUid)
                .collect(Collectors.toSet());
        
        // 检查请求内部是否有重复的NFC UID
        if (requestNfcUids.size() != request.crates().size()) {
            Map<String, Long> nfcUidCounts = request.crates().stream()
                    .collect(Collectors.groupingBy(CrateRequestDTO::nfcUid, Collectors.counting()));
            
            nfcUidCounts.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .forEach(entry -> {
                        failures.add(BatchRegisterResponseDTO.BatchRegisterFailureDTO.of(
                                entry.getKey(), 
                                "请求中存在重复的NFC UID", 
                                "DUPLICATE_IN_REQUEST"
                        ));
                    });
        }
        
        // 检查数据库中是否已存在这些NFC UID
        List<String> existingNfcUids = crateRepository.findNfcUidsByTenantIdAndNfcUidIn(tenantId, requestNfcUids);
        Set<String> existingNfcUidSet = new HashSet<>(existingNfcUids);
        
        // 逐个处理注册请求
        for (CrateRequestDTO crateRequest : request.crates()) {
            try {
                // 跳过已经在失败列表中的NFC UID
                if (failures.stream().anyMatch(f -> f.nfcUid().equals(crateRequest.nfcUid()))) {
                    continue;
                }
                
                // 检查是否已存在
                if (existingNfcUidSet.contains(crateRequest.nfcUid())) {
                    failures.add(BatchRegisterResponseDTO.BatchRegisterFailureDTO.of(
                            crateRequest.nfcUid(), 
                            "NFC UID在当前租户内已存在", 
                            "DUPLICATE_NFC_UID"
                    ));
                    continue;
                }
                
                // 执行单个注册（不使用原有的registerCrate方法，避免重复检查）
                Crate crate = new Crate();
                crate.setTenantId(tenantId);
                crate.setNfcUid(crateRequest.nfcUid());
                crate.setStatus(CrateStatus.AVAILABLE);
                
                // 设置周转筐类型
                if (crateRequest.crateTypeId() != null) {
                    CrateType crateType = crateTypeRepository.findByIdAndTenantId(crateRequest.crateTypeId(), tenantId)
                            .orElseThrow(() -> new IllegalArgumentException("周转筐类型不存在: " + crateRequest.crateTypeId()));
                    crate.setCrateType(crateType);
                }
                
                crate = crateRepository.save(crate);
                successfulCrates.add(convertToDTO(crate));
                
                // 添加到已存在集合，避免后续重复
                existingNfcUidSet.add(crateRequest.nfcUid());
                
            } catch (Exception e) {
                log.warn("批量注册中单个周转筐失败: nfcUid={}, error={}", crateRequest.nfcUid(), e.getMessage());
                failures.add(BatchRegisterResponseDTO.BatchRegisterFailureDTO.of(
                        crateRequest.nfcUid(), 
                        e.getMessage(), 
                        "REGISTRATION_ERROR"
                ));
            }
        }
        
        log.info("批量注册完成: 总数={}, 成功={}, 失败={}", 
                request.crates().size(), successfulCrates.size(), failures.size());
        
        return BatchRegisterResponseDTO.success(request.crates().size(), successfulCrates, failures);
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

    @Override
    @Transactional
    public CrateDTO deactivateCrate(Long crateId, Long tenantId, String reason) {
        log.info("报废周转筐: crateId={}, tenantId={}, reason={}", crateId, tenantId, reason);
        
        Crate crate = crateRepository.findByIdAndTenantId(crateId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("周转筐不存在"));

        // 检查周转筐当前状态
        if (crate.getStatus() == CrateStatus.INACTIVE) {
            throw new IllegalStateException("周转筐已经处于报废状态");
        }

        // 如果周转筐正在使用中，需要先清空内容
        if (crate.getStatus() == CrateStatus.IN_USE) {
            log.warn("报废使用中的周转筐，将清空其内容: crateId={}", crateId);
            // 清空周转筐内容
            crateContentRepository.findByCrateId(crateId).ifPresent(content -> {
                crateContentRepository.delete(content);
                log.info("已清空报废周转筐的内容: crateId={}", crateId);
            });
        }

        // 设置为报废状态
        crate.setStatus(CrateStatus.INACTIVE);
        
        // 如果有报废原因，可以记录到备注字段（如果Crate实体有notes字段的话）
        // crate.setNotes(reason);
        
        crate = crateRepository.save(crate);
        log.info("周转筐报废成功: crateId={}, 原状态={}", crateId, crate.getStatus());

        return convertToDTO(crate);
    }

    @Override
    @Transactional
    public int batchDeactivateCrates(List<Long> crateIds, Long tenantId, String reason) {
        log.info("批量报废周转筐: crateIds={}, tenantId={}, reason={}", crateIds, tenantId, reason);
        
        if (crateIds == null || crateIds.isEmpty()) {
            throw new IllegalArgumentException("周转筐ID列表不能为空");
        }

        int successCount = 0;
        int failCount = 0;

        for (Long crateId : crateIds) {
            try {
                deactivateCrate(crateId, tenantId, reason);
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("批量报废中单个周转筐失败: crateId={}, error={}", crateId, e.getMessage());
            }
        }

        log.info("批量报废完成: 成功={}, 失败={}, 总数={}", successCount, failCount, crateIds.size());
        return successCount;
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
                content.getQuantity() != null ? content.getQuantity() : null,
                content.getStatus(),
                content.getLocation() != null ? content.getLocation().getId() : null,
                content.getLocation() != null ? content.getLocation().getName() : null,
                content.getLastUpdatedAt()
        );
    }
}