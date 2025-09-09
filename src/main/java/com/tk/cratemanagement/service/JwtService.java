package com.tk.cratemanagement.service;

import com.tk.cratemanagement.domain.enumeration.UserRole;

/**
 * JWT服务接口
 * 处理JWT token的生成、验证和解析
 */
public interface JwtService {

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param role 用户角色
     * @return JWT token字符串
     */
    String generateToken(Long userId, Long tenantId, UserRole role);

    /**
     * 验证JWT token是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 从token中提取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);

    /**
     * 从token中提取租户ID
     *
     * @param token JWT token
     * @return 租户ID
     */
    Long getTenantIdFromToken(String token);

    /**
     * 从token中提取用户角色
     *
     * @param token JWT token
     * @return 用户角色
     */
    UserRole getRoleFromToken(String token);
}