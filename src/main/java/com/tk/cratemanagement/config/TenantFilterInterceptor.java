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
            // 优先从请求头获取租户ID（用于特殊情况）
            String tenantIdHeader = request.getHeader("X-Tenant-ID");
            if (tenantIdHeader != null) {
                try {
                    Long tenantId = Long.parseLong(tenantIdHeader);
                    TenantContext.setCurrentTenant(tenantId);
                    log.debug("从请求头设置租户上下文: tenantId={}", tenantId);
                } catch (NumberFormatException e) {
                    log.warn("无效的租户ID: {}", tenantIdHeader);
                }
            }
            // 注意：不再设置默认租户ID，让JwtAuthenticationFilter来处理
            // 如果既没有X-Tenant-ID请求头，也没有JWT token，则TenantContext保持为null
            // AuthUtils会在这种情况下使用默认值1L作为后备方案
            
            filterChain.doFilter(request, response);
        } finally {
            // 清除租户上下文
            TenantContext.clear();
        }
    }
}
