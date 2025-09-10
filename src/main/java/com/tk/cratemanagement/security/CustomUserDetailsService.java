package com.tk.cratemanagement.security;

import com.tk.cratemanagement.domain.User;
import com.tk.cratemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 * 从数据库加载用户信息用于认证和授权
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户ID加载用户详情
     * 注意：这里的username参数实际上是用户ID
     *
     * @param username 用户ID（字符串形式）
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Long userId = Long.parseLong(username);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在，ID: " + userId));

            // 检查用户是否激活
            if (!user.isActive()) {
                throw new UsernameNotFoundException("用户已被禁用，ID: " + userId);
            }

            log.debug("成功加载用户详情，用户ID: {}, 邮箱: {}, 角色: {}", 
                    user.getId(), user.getEmail(), user.getRole());

            // 构建Spring Security的UserDetails对象
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getId().toString()) // 使用用户ID作为username
                    .password(user.getPasswordHash()) // 密码哈希
                    .authorities(Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    )) // 角色权限
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!user.isActive()) // 根据isActive字段设置是否禁用
                    .build();

        } catch (NumberFormatException e) {
            log.error("无效的用户ID格式: {}", username);
            throw new UsernameNotFoundException("无效的用户ID格式: " + username);
        }
    }

    /**
     * 根据邮箱加载用户详情
     * 用于登录时的用户验证
     *
     * @param email 用户邮箱
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在，邮箱: " + email));

        // 检查用户是否激活
        if (!user.isActive()) {
            throw new UsernameNotFoundException("用户已被禁用，邮箱: " + email);
        }

        log.debug("成功加载用户详情，邮箱: {}, 用户ID: {}, 角色: {}", 
                email, user.getId(), user.getRole());

        // 构建Spring Security的UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getId().toString()) // 使用用户ID作为username
                .password(user.getPasswordHash()) // 密码哈希
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )) // 角色权限
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive()) // 根据isActive字段设置是否禁用
                .build();
    }
}