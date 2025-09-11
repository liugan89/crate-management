package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.config.TenantContext;
import com.tk.cratemanagement.domain.Tenant;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;
import com.tk.cratemanagement.dto.TenantDTO;
import com.tk.cratemanagement.dto.TenantUpdateDTO;
import com.tk.cratemanagement.repository.TenantRepository;
import com.tk.cratemanagement.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for managing Tenant entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    
    private final TenantRepository tenantRepository;
    
    @Override
    @Transactional(readOnly = true)
    public TenantDTO getCurrentTenant() {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("当前用户未关联租户");
        }
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        
        return convertToDTO(tenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TenantDTO getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        
        return convertToDTO(tenant);
    }
    
    @Override
    @Transactional
    public TenantDTO updateCurrentTenant(TenantUpdateDTO updateDTO) {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("当前用户未关联租户");
        }
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        
        // 检查公司名称是否已被其他租户使用
        if (updateDTO.companyName() != null && 
            !updateDTO.companyName().equals(tenant.getCompanyName()) &&
            tenantRepository.existsByCompanyNameAndIdNot(updateDTO.companyName(), tenantId)) {
            throw new IllegalArgumentException("公司名称已被使用");
        }
        
        // 检查联系邮箱是否已被其他租户使用
        if (updateDTO.contactEmail() != null && 
            !updateDTO.contactEmail().equals(tenant.getContactEmail()) &&
            tenantRepository.existsByContactEmailAndIdNot(updateDTO.contactEmail(), tenantId)) {
            throw new IllegalArgumentException("联系邮箱已被使用");
        }
        
        // 更新租户信息
        updateTenantFromDTO(tenant, updateDTO);
        tenant = tenantRepository.save(tenant);
        
        log.info("租户信息更新成功: tenantId={}", tenantId);
        return convertToDTO(tenant);
    }
    
    @Override
    @Transactional
    public TenantDTO updateTenantStatus(Long tenantId, TenantStatus status) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        
        tenant.setStatus(status);
        tenant = tenantRepository.save(tenant);
        
        log.info("租户状态更新成功: tenantId={}, status={}", tenantId, status);
        return convertToDTO(tenant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> getTenantsByStatus(TenantStatus status) {
        return tenantRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    @Transactional
    public void deleteTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        
        tenant.setDeletedAt(LocalDateTime.now());
        tenantRepository.save(tenant);
        
        log.info("租户软删除成功: tenantId={}", tenantId);
    }
    
    /**
     * 将 Tenant 实体转换为 DTO
     */
    private TenantDTO convertToDTO(Tenant tenant) {
        return new TenantDTO(
                tenant.getId(),
                tenant.getCompanyName(),
                tenant.getContactEmail(),
                tenant.getPhoneNumber(),
                tenant.getAddress(),
                tenant.getCity(),
                tenant.getState(),
                tenant.getZipCode(),
                tenant.getCountry(),
                tenant.getTimezone(),
                tenant.getStatus(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
    
    /**
     * 从 DTO 更新 Tenant 实体
     */
    private void updateTenantFromDTO(Tenant tenant, TenantUpdateDTO updateDTO) {
        if (updateDTO.companyName() != null) {
            tenant.setCompanyName(updateDTO.companyName());
        }
        if (updateDTO.contactEmail() != null) {
            tenant.setContactEmail(updateDTO.contactEmail());
        }
        if (updateDTO.phoneNumber() != null) {
            tenant.setPhoneNumber(updateDTO.phoneNumber());
        }
        if (updateDTO.address() != null) {
            tenant.setAddress(updateDTO.address());
        }
        if (updateDTO.city() != null) {
            tenant.setCity(updateDTO.city());
        }
        if (updateDTO.state() != null) {
            tenant.setState(updateDTO.state());
        }
        if (updateDTO.zipCode() != null) {
            tenant.setZipCode(updateDTO.zipCode());
        }
        if (updateDTO.country() != null) {
            tenant.setCountry(updateDTO.country());
        }
        if (updateDTO.timezone() != null) {
            tenant.setTimezone(updateDTO.timezone());
        }
    }
}