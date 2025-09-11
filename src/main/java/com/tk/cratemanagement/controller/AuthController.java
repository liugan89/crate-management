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
    @Operation(
        summary = "租户注册", 
        description = "注册新租户和管理员用户。创建租户时会同时创建管理员账户和14天试用订阅。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "注册请求信息，包含完整的公司信息和管理员账户信息",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "注册示例",
                    value = """
                    {
                        "companyName": "示例科技有限公司",
                        "contactEmail": "contact@example.com",
                        "phoneNumber": "+86-400-123-4567",
                        "address": "北京市朝阳区示例大厦1001室",
                        "city": "北京",
                        "state": "北京市",
                        "zipCode": "100000",
                        "country": "CN",
                        "timezone": "Asia/Shanghai",
                        "email": "admin@example.com",
                        "password": "SecurePassword123",
                        "fullName": "张三",
                        "phone": "+86-138-0013-8000"
                    }
                    """
                )
            )
        )
    )
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