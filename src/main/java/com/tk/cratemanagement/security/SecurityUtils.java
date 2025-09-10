package com.tk.cratemanagement.security;

import com.tk.cratemanagement.config.TenantContext;
import com.tk.cratemanagement.domain.enumeration.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 安全工具类
 * 提供获取当前认证用户信息的便捷方法
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 获取当前认证用户的ID
     *
     * @return 当前用户ID，如果未认证则返回空
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .map(username -> {
                    try {
                        return Long.parseLong(username);
                    } catch (NumberFormatException e) {
                        log.warn("无法解析用户ID: {}", username);
                        return null;
                    }
                });
    }

    /**
     * 获取当前认证用户的租户ID
     *
     * @return 当前租户ID，如果未设置则返回空
     */
    public static Optional<Long> getCurrentTenantId() {
        return Optional.ofNullable(TenantContext.getCurrentTenantId());
    }

    /**
     * 获取当前认证用户的角色
     *
     * @return 当前用户角色，如果未认证则返回空
     */
    public static Optional<UserRole> getCurrentUserRole() {
        return getCurrentAuthentication()
                .map(Authentication::getAuthorities)
                .flatMap(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .map(authority -> authority.substring(5)) // 移除"ROLE_"前缀
                        .map(roleName -> {
                            try {
                                return UserRole.valueOf(roleName);
                            } catch (IllegalArgumentException e) {
                                log.warn("无法解析用户角色: {}", roleName);
                                return null;
                            }
                        })
                        .filter(role -> role != null)
                        .findFirst()
                );
    }

    /**
     * 检查当前用户是否具有指定角色
     *
     * @param role 要检查的角色
     * @return 如果用户具有指定角色则返回true
     */
    public static boolean hasRole(UserRole role) {
        return getCurrentUserRole()
                .map(currentRole -> currentRole == role)
                .orElse(false);
    }

    /**
     * 检查当前用户是否为管理员
     *
     * @return 如果用户是管理员则返回true
     */
    public static boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    /**
     * 检查当前用户是否为操作员
     *
     * @return 如果用户是操作员则返回true
     */
    public static boolean isOperator() {
        return hasRole(UserRole.OPERATOR);
    }

    /**
     * 检查当前用户是否已认证
     *
     * @return 如果用户已认证则返回true
     */
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * 获取当前认证信息
     *
     * @return 当前认证信息，如果未认证则返回空
     */
    private static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of(authentication);
        }
        return Optional.empty();
    }

    /**
     * 获取当前用户的用户名（实际上是用户ID）
     *
     * @return 当前用户名，如果未认证则返回空
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername);
    }
}