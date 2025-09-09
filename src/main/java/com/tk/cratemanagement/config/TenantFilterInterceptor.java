package com.tk.cratemanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 租户过滤器拦截器
 * 从请求中提取租户ID并设置到上下文中
 * 在JWT认证实现后，这里会从JWT token中提取租户ID
 */
@Slf4j
@Component
public class TenantFilterInterceptor extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 临时实现：从请求头中获取租户ID
            // 在实际JWT实现中，这里会从JWT token中解析租户ID
            String tenantIdHeader = request.getHeader("X-Tenant-ID");
            if (tenantIdHeader != null) {
                try {
                    Long tenantId = Long.parseLong(tenantIdHeader);
                    TenantContext.setCurrentTenant(tenantId);
                    log.debug("设置租户上下文: tenantId={}", tenantId);
                } catch (NumberFormatException e) {
                    log.warn("无效的租户ID: {}", tenantIdHeader);
                }
            } else {
                // 临时默认租户ID，用于测试
                TenantContext.setCurrentTenant(1L);
                log.debug("使用默认租户ID: 1");
            }
            
            filterChain.doFilter(request, response);
        } finally {
            // 清除租户上下文
            TenantContext.clear();
        }
    }
}
