package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.CreateUserRequestDTO;
import com.tk.cratemanagement.dto.ResetPasswordRequestDTO;
import com.tk.cratemanagement.dto.UpdateUserRequestDTO;
import com.tk.cratemanagement.dto.UserDTO;
import com.tk.cratemanagement.security.SecurityUtils;
import com.tk.cratemanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 处理租户内用户管理的REST API端点
 * 只有ADMIN角色可以访问这些端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "用户管理", description = "租户内用户管理相关API")
public class UserController {

    private final UserService userService;

    /**
     * 创建新用户
     * 在管理员的租户内创建新用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "在当前租户内创建新用户")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.info("收到创建用户请求: email={}, tenantId={}", request.email(), tenantId);
        
        UserDTO response = userService.createUser(request, tenantId);
        
        log.info("用户创建成功: userId={}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 获取租户内所有用户
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "获取当前租户内所有用户")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.debug("获取用户列表: tenantId={}", tenantId);
        
        List<UserDTO> users = userService.getAllUsers(tenantId);
        
        return ResponseEntity.ok(users);
    }

    /**
     * 获取指定用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "获取指定用户的详细信息")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.debug("获取用户详情: userId={}, tenantId={}", userId, tenantId);
        
        UserDTO user = userService.getUserById(userId, tenantId);
        
        return ResponseEntity.ok(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户信息", description = "更新用户的角色和状态")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId,
                                            @Valid @RequestBody UpdateUserRequestDTO request) {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.info("收到更新用户请求: userId={}, tenantId={}", userId, tenantId);
        
        UserDTO response = userService.updateUser(userId, request, tenantId);
        
        log.info("用户信息更新成功: userId={}", userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "从当前租户中删除用户")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.info("收到删除用户请求: userId={}, tenantId={}", userId, tenantId);
        
        userService.deleteUser(userId, tenantId);
        
        log.info("用户删除成功: userId={}", userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{userId}/reset-password")
    @Operation(summary = "重置用户密码", description = "重置指定用户的密码")
    public ResponseEntity<Void> resetPassword(@PathVariable Long userId,
                                            @Valid @RequestBody ResetPasswordRequestDTO request) {
        Long tenantId = SecurityUtils.getCurrentTenantId()
                .orElseThrow(() -> new IllegalStateException("无法获取当前租户ID"));
        log.info("收到重置密码请求: userId={}, tenantId={}", userId, tenantId);
        
        userService.resetPassword(userId, request, tenantId);
        
        log.info("用户密码重置成功: userId={}", userId);
        return ResponseEntity.noContent().build();
    }
}