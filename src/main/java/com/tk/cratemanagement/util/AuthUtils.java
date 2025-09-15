package com.tk.cratemanagement.util;

import com.tk.cratemanagement.config.TenantContext;
import com.tk.cratemanagement.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证工具类
 * 用于从认证上下文中提取用户信息
 */
@Component
public class AuthUtils {
    
    private static JwtService jwtService;
    
    @Autowired
    public void setJwtService(JwtService jwtService) {
        AuthUtils.jwtService = jwtService;
    }
    
    /**
     * 从认证信息中提取租户ID
     * 优先从TenantContext获取，如果没有则从JWT token解析
     */
    public static Long getTenantIdFromAuth(Authentication authentication) {
        System.out.println("=== DEBUG getTenantIdFromAuth 开始 ===");
        
        // 首先尝试从TenantContext获取（由JwtAuthenticationFilter设置）
        Long tenantId = TenantContext.getCurrentTenantId();
        System.out.println("TenantContext.getCurrentTenantId(): " + tenantId);
        if (tenantId != null) {
            System.out.println("从TenantContext返回租户ID: " + tenantId);
            return tenantId;
        }
        
        // 如果TenantContext中没有，尝试从JWT token解析
        String token = extractTokenFromRequest();
        System.out.println("提取的token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        System.out.println("jwtService 是否为null: " + (jwtService == null));
        
        if (token != null && jwtService != null && jwtService.validateToken(token)) {
            Long tokenTenantId = jwtService.getTenantIdFromToken(token);
            System.out.println("从JWT token解析的租户ID: " + tokenTenantId);
            return tokenTenantId;
        }
        
        // 最后的备选方案：返回默认租户ID（用于开发/测试）
        System.out.println("返回默认租户ID: 1");
        System.out.println("=== DEBUG getTenantIdFromAuth 结束 ===");
        return 1L;
    }
    
    /**
     * 从认证信息中提取用户ID
     * 优先从JWT token解析用户ID
     */
    public static Long getUserIdFromAuth(Authentication authentication) {
        // 尝试从JWT token解析
        String token = extractTokenFromRequest();
        if (token != null && jwtService != null && jwtService.validateToken(token)) {
            return jwtService.getUserIdFromToken(token);
        }
        
        // 备选方案：从Authentication对象的principal获取
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            try {
                return Long.parseLong((String) authentication.getPrincipal());
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        // 最后的备选方案：返回默认用户ID（用于开发/测试）
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
    
    /**
     * 从当前HTTP请求中提取JWT token
     */
    private static String extractTokenFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader.substring(7);
                }
            }
        } catch (Exception e) {
            // 忽略错误，返回null
        }
        return null;
    }
}
