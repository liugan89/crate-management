package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.AuthResponseDTO;
import com.tk.cratemanagement.dto.LoginRequestDTO;
import com.tk.cratemanagement.dto.RegisterRequestDTO;

/**
 * 认证服务接口
 * 处理用户认证、租户注册等核心认证业务逻辑
 */
public interface AuthService {

    /**
     * 注册新租户和管理员用户
     * 创建租户、ADMIN角色用户和14天试用订阅
     *
     * @param request 注册请求
     * @return 认证响应，包含JWT token
     */
    AuthResponseDTO register(RegisterRequestDTO request);

    /**
     * 用户登录认证
     * 验证凭据并返回JWT token
     *
     * @param request 登录请求
     * @return 认证响应，包含JWT token
     */
    AuthResponseDTO login(LoginRequestDTO request);
}