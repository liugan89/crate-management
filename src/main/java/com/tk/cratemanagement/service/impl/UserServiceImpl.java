package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.User;
import com.tk.cratemanagement.dto.CreateUserRequestDTO;
import com.tk.cratemanagement.dto.ResetPasswordRequestDTO;
import com.tk.cratemanagement.dto.UpdateUserRequestDTO;
import com.tk.cratemanagement.dto.UserDTO;
import com.tk.cratemanagement.repository.UserRepository;
import com.tk.cratemanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 * 实现租户内用户的增删改查业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequestDTO request, Long tenantId) {
        log.info("创建新用户: email={}, tenantId={}", request.email(), tenantId);

        // 检查邮箱在租户内是否已存在
        if (userRepository.findOneByTenantIdAndEmail(tenantId, request.email()).isPresent()) {
            throw new IllegalArgumentException("邮箱在当前租户内已存在");
        }

        User user = new User();
        user.setTenantId(tenantId);
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.initialPassword()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setActive(true);

        user = userRepository.save(user);
        log.info("用户创建成功: userId={}", user.getId());

        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(Long tenantId) {
        log.debug("获取租户用户列表: tenantId={}", tenantId);
        
        List<User> users = userRepository.findByTenantId(tenantId);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId, Long tenantId) {
        log.debug("获取用户详情: userId={}, tenantId={}", userId, tenantId);
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO request, Long tenantId) {
        log.info("更新用户信息: userId={}, tenantId={}", userId, tenantId);
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.isActive() != null) {
            user.setActive(request.isActive());
        }

        user = userRepository.save(user);
        log.info("用户信息更新成功: userId={}", user.getId());

        return convertToDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long tenantId) {
        log.info("软删除用户: userId={}, tenantId={}", userId, tenantId);
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 软删除：设置deleted_at时间戳
        user.setDeletedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        
        log.info("用户软删除成功: userId={}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, ResetPasswordRequestDTO request, Long tenantId) {
        log.info("重置用户密码: userId={}, tenantId={}", userId, tenantId);
        
        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        
        log.info("用户密码重置成功: userId={}", userId);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.isActive(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}