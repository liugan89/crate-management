package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.TenantDTO;
import com.tk.cratemanagement.dto.TenantUpdateDTO;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;

import java.util.List;

/**
 * Service interface for managing Tenant entities.
 */
public interface TenantService {
    
    /**
     * 获取当前租户信息
     */
    TenantDTO getCurrentTenant();
    
    /**
     * 根据ID获取租户信息
     */
    TenantDTO getTenantById(Long id);
    
    /**
     * 更新当前租户信息
     */
    TenantDTO updateCurrentTenant(TenantUpdateDTO updateDTO);
    
    /**
     * 更新租户状态（仅系统管理员可用）
     */
    TenantDTO updateTenantStatus(Long tenantId, TenantStatus status);
    
    /**
     * 获取所有租户列表（仅系统管理员可用）
     */
    List<TenantDTO> getAllTenants();
    
    /**
     * 根据状态获取租户列表（仅系统管理员可用）
     */
    List<TenantDTO> getTenantsByStatus(TenantStatus status);
    
    /**
     * 软删除租户（仅系统管理员可用）
     */
    void deleteTenant(Long tenantId);
}