package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.SyncRequestDTO;
import com.tk.cratemanagement.dto.SyncResponseDTO;
import com.tk.cratemanagement.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 离线同步控制器
 * 处理移动端离线操作批量同步的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "离线同步", description = "移动端离线操作同步相关API")
public class SyncController {

    private final SyncService syncService;

    /**
     * 处理离线同步请求
     * 批量处理移动端提交的离线操作
     */
    @PostMapping("/sync")
    @Operation(summary = "离线同步", description = "批量同步移动端的离线操作")
    public ResponseEntity<SyncResponseDTO> processSync(@Valid @RequestBody SyncRequestDTO request,
                                                     Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        Long userId = getUserIdFromAuth(authentication);
        log.info("收到离线同步请求: tenantId={}, userId={}, operationCount={}", 
                tenantId, userId, request.operations().size());
        
        SyncResponseDTO response = syncService.processSync(request, tenantId, userId);
        
        log.info("离线同步处理完成: tenantId={}, success={}", tenantId, response.success());
        return ResponseEntity.ok(response);
    }

    /**
     * 获取同步状态
     * 查询当前租户的同步状态信息
     */
    @GetMapping("/sync/status")
    @Operation(summary = "同步状态", description = "查询当前租户的同步状态信息")
    public ResponseEntity<Object> getSyncStatus(Authentication authentication) {
        Long tenantId = getTenantIdFromAuth(authentication);
        log.info("查询同步状态: tenantId={}", tenantId);
        
        // 返回简单的状态信息
        var status = new Object() {
            public final String status = "online";
            public final String lastSyncTime = java.time.Instant.now().toString();
            public final Long tenantId = getTenantIdFromAuth(authentication);
        };
        
        return ResponseEntity.ok(status);
    }

    /**
     * 从认证信息中提取租户ID
     */
    private Long getTenantIdFromAuth(Authentication authentication) {
        // 使用AuthUtils工具类从认证信息中提取租户ID
        return com.tk.cratemanagement.util.AuthUtils.getTenantIdFromAuth(authentication);
    }

    /**
     * 从认证信息中提取用户ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        // TODO: 从JWT token中提取用户ID
        return 1L; // 临时返回
    }
}