package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.CreateUserRequestDTO;
import com.tk.cratemanagement.dto.ResetPasswordRequestDTO;
import com.tk.cratemanagement.dto.UpdateUserRequestDTO;
import com.tk.cratemanagement.dto.UserDTO;

import java.util.List;

/**
 * 用户管理服务接口
 * 处理租户内用户的增删改查操作
 */
public interface UserService {

    /**
     * 创建新用户
     * 只有ADMIN角色可以创建用户
     *
     * @param request 创建用户请求
     * @param tenantId 当前租户ID
     * @return 用户DTO
     */
    UserDTO createUser(CreateUserRequestDTO request, Long tenantId);

    /**
     * 获取租户内所有用户列表
     *
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<UserDTO> getAllUsers(Long tenantId);

    /**
     * 根据ID获取用户详情
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long userId, Long tenantId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @param tenantId 租户ID
     * @return 更新后的用户DTO
     */
    UserDTO updateUser(Long userId, UpdateUserRequestDTO request, Long tenantId);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void deleteUser(Long userId, Long tenantId);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param request 重置密码请求
     * @param tenantId 租户ID
     */
    void resetPassword(Long userId, ResetPasswordRequestDTO request, Long tenantId);
}