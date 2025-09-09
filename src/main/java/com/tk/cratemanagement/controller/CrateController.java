package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.*;
import com.tk.cratemanagement.service.CrateService;
import com.tk.cratemanagement.util.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 周转筐管理控制器
 * 处理周转筐注册、查询和管理的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "周转筐管理", description = "周转筐和周转筐类型管理相关API")
public class CrateController {

    private final CrateService crateService;

    // ========== 周转筐管理 ==========

    /**
     * 注册新的周转筐
     */
    @PostMapping("/crates")
    @Operation(summary = "注册周转筐", description = "注册新的周转筐实例")
    public ResponseEntity<CrateDTO> registerCrate(@Valid @RequestBody CrateRequestDTO request,
                                                Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到注册周转筐请求: nfcUid={}, tenantId={}", request.nfcUid(), tenantId);
        
        CrateDTO response = crateService.registerCrate(request, tenantId);
        
        log.info("周转筐注册成功: crateId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取所有周转筐
     */
    @GetMapping("/crates")
    @Operation(summary = "获取周转筐列表", description = "获取当前租户的所有周转筐")
    public ResponseEntity<List<CrateDTO>> getAllCrates(Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("获取周转筐列表: tenantId={}", tenantId);
        
        List<CrateDTO> crates = crateService.getAllCrates(tenantId);
        
        return ResponseEntity.ok(crates);
    }

    /**
     * 获取周转筐详情
     */
    @GetMapping("/crates/{id}")
    @Operation(summary = "获取周转筐详情", description = "获取指定周转筐的详细信息")
    public ResponseEntity<CrateDTO> getCrateById(@PathVariable Long id,
                                               Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("获取周转筐详情: crateId={}, tenantId={}", id, tenantId);
        
        CrateDTO crate = crateService.getCrateById(id, tenantId);
        
        return ResponseEntity.ok(crate);
    }

    /**
     * 更新周转筐信息
     */
    @PutMapping("/crates/{id}")
    @Operation(summary = "更新周转筐信息", description = "更新周转筐的基本信息")
    public ResponseEntity<CrateDTO> updateCrate(@PathVariable Long id,
                                              @Valid @RequestBody CrateRequestDTO request,
                                              Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到更新周转筐请求: crateId={}, tenantId={}", id, tenantId);
        
        CrateDTO response = crateService.updateCrate(id, request, tenantId);
        
        log.info("周转筐信息更新成功: crateId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据NFC UID查询周转筐详情
     * 高频操作：移动端扫码查询
     */
    @GetMapping("/crates/lookup")
    @Operation(summary = "NFC查询周转筐", description = "根据NFC UID查询周转筐详情（高频操作）")
    public ResponseEntity<CrateDetailsDTO> lookupCrateByNfcUid(
            @RequestParam @NotBlank String nfc_uid,
            Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("NFC查询周转筐: nfcUid={}, tenantId={}", nfc_uid, tenantId);
        
        CrateDetailsDTO details = crateService.lookupCrateByNfcUid(nfc_uid, tenantId);
        
        return ResponseEntity.ok(details);
    }

    // ========== 周转筐类型管理 ==========

    /**
     * 创建周转筐类型
     */
    @PostMapping("/crate-types")
    @Operation(summary = "创建周转筐类型", description = "创建新的周转筐类型模板")
    public ResponseEntity<CrateTypeDTO> createCrateType(@Valid @RequestBody CrateTypeRequestDTO request,
                                                       Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到创建周转筐类型请求: name={}, tenantId={}", request.name(), tenantId);
        
        CrateTypeDTO response = crateService.createCrateType(request, tenantId);
        
        log.info("周转筐类型创建成功: typeId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取所有周转筐类型
     */
    @GetMapping("/crate-types")
    @Operation(summary = "获取周转筐类型列表", description = "获取当前租户的所有周转筐类型")
    public ResponseEntity<List<CrateTypeDTO>> getAllCrateTypes(Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.debug("获取周转筐类型列表: tenantId={}", tenantId);
        
        List<CrateTypeDTO> crateTypes = crateService.getAllCrateTypes(tenantId);
        
        return ResponseEntity.ok(crateTypes);
    }

    /**
     * 更新周转筐类型
     */
    @PutMapping("/crate-types/{id}")
    @Operation(summary = "更新周转筐类型", description = "更新周转筐类型的基本信息")
    public ResponseEntity<CrateTypeDTO> updateCrateType(@PathVariable Long id,
                                                       @Valid @RequestBody CrateTypeRequestDTO request,
                                                       Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到更新周转筐类型请求: typeId={}, tenantId={}", id, tenantId);
        
        CrateTypeDTO response = crateService.updateCrateType(id, request, tenantId);
        
        log.info("周转筐类型更新成功: typeId={}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除周转筐类型
     */
    @DeleteMapping("/crate-types/{id}")
    @Operation(summary = "删除周转筐类型", description = "删除指定的周转筐类型")
    public ResponseEntity<Void> deleteCrateType(@PathVariable Long id,
                                              Authentication authentication) {
        Long tenantId = AuthUtils.getTenantIdFromAuth(authentication);
        log.info("收到删除周转筐类型请求: typeId={}, tenantId={}", id, tenantId);
        
        crateService.deleteCrateType(id, tenantId);
        
        log.info("周转筐类型删除成功: typeId={}", id);
        return ResponseEntity.noContent().build();
    }

}