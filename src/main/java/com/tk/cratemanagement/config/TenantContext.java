package com.tk.cratemanagement.config;

/**
 * 租户上下文
 * 用于存储当前请求的租户ID，供多租户过滤器使用
 */
public class TenantContext {
    
    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    
    /**
     * 设置当前租户ID
     */
    public static void setCurrentTenant(Long tenantId) {
        currentTenant.set(tenantId);
    }
    
    /**
     * 获取当前租户ID
     */
    public static Long getCurrentTenant() {
        return currentTenant.get();
    }
    
    /**
     * 清除当前租户ID
     */
    public static void clear() {
        currentTenant.remove();
    }
}
