package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.dto.AuthResponseDTO;
import com.tk.cratemanagement.dto.LoginRequestDTO;
import com.tk.cratemanagement.dto.RegisterRequestDTO;
import com.tk.cratemanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户认证和租户注册的REST API端点
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证和租户注册相关API")
public class AuthController {

    private final AuthService authService;

    /**
     * 注册新租户和管理员用户
     * 公开接口，用于创建租户、ADMIN角色用户和14天试用订阅
     */
    @PostMapping("/register")
    @Operation(summary = "租户注册", description = "注册新租户和管理员用户")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("收到租户注册请求: companyName={}, email={}", request.companyName(), request.email());
        
        AuthResponseDTO response = authService.register(request);
        
        log.info("租户注册成功: companyName={}", request.companyName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 用户登录
     * 验证用户凭据并返回JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户凭据并返回JWT token")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("收到用户登录请求: email={}", request.email());
        
        AuthResponseDTO response = authService.login(request);
        
        log.info("用户登录成功: email={}", request.email());
        return ResponseEntity.ok(response);
    }
}