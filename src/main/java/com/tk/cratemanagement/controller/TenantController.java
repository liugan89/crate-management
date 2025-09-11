package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.TenantDTO;
import com.tk.cratemanagement.dto.TenantUpdateDTO;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;
import com.tk.cratemanagement.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Tenant entities.
 */
@Tag(name = "租户管理", description = "租户信息管理相关接口")
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {
    
    private final TenantService tenantService;
    
    @Operation(summary = "获取当前租户信息", description = "获取当前登录用户所属租户的详细信息")
    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<TenantDTO> getCurrentTenant() {
        TenantDTO tenant = tenantService.getCurrentTenant();
        return ResponseEntity.ok(tenant);
    }
    
    @Operation(summary = "更新当前租户信息", description = "更新当前登录用户所属租户的信息")
    @PutMapping("/current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDTO> updateCurrentTenant(@Valid @RequestBody TenantUpdateDTO updateDTO) {
        TenantDTO updatedTenant = tenantService.updateCurrentTenant(updateDTO);
        return ResponseEntity.ok(updatedTenant);
    }
    
    @Operation(summary = "获取所有租户列表", description = "获取系统中所有租户的列表（仅系统管理员）")
    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }
    
    @Operation(summary = "根据ID获取租户信息", description = "根据租户ID获取租户详细信息（仅系统管理员）")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<TenantDTO> getTenantById(
            @Parameter(description = "租户ID") @PathVariable Long id) {
        TenantDTO tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }
    
    @Operation(summary = "根据状态获取租户列表", description = "根据租户状态获取租户列表（仅系统管理员）")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<TenantDTO>> getTenantsByStatus(
            @Parameter(description = "租户状态") @PathVariable TenantStatus status) {
        List<TenantDTO> tenants = tenantService.getTenantsByStatus(status);
        return ResponseEntity.ok(tenants);
    }
    
    @Operation(summary = "更新租户状态", description = "更新指定租户的状态（仅系统管理员）")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<TenantDTO> updateTenantStatus(
            @Parameter(description = "租户ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam TenantStatus status) {
        TenantDTO updatedTenant = tenantService.updateTenantStatus(id, status);
        return ResponseEntity.ok(updatedTenant);
    }
    
    @Operation(summary = "删除租户", description = "软删除指定租户（仅系统管理员）")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteTenant(
            @Parameter(description = "租户ID") @PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}