package com.tk.cratemanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 认证工具类
 * 用于从认证上下文中提取用户信息
 * 在JWT认证未实现前，提供临时的认证信息提取机制
 */
public class AuthUtils {
    
    /**
     * 从认证信息中提取租户ID
     * 临时实现：返回默认租户ID
     * TODO: 实现真正的JWT token解析
     */
    public static Long getTenantIdFromAuth(Authentication authentication) {
        // 临时实现：返回默认租户ID
        // 在实际JWT实现中，这里应该从token中解析租户ID
        return 1L;
    }
    
    /**
     * 从认证信息中提取用户ID
     * 临时实现：返回默认用户ID
     * TODO: 实现真正的JWT token解析
     */
    public static Long getUserIdFromAuth(Authentication authentication) {
        // 临时实现：返回默认用户ID
        // 在实际JWT实现中，这里应该从token中解析用户ID
        return 1L;
    }
    
    /**
     * 从当前安全上下文中获取租户ID
     */
    public static Long getCurrentTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getTenantIdFromAuth(auth);
    }
    
    /**
     * 从当前安全上下文中获取用户ID
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getUserIdFromAuth(auth);
    }
}
