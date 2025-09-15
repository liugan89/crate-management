package com.tk.cratemanagement.security;

import com.tk.cratemanagement.config.TenantContext;
import com.tk.cratemanagement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 从请求头中提取JWT token，验证并设置认证信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        // final String userEmail;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT token
        jwt = authHeader.substring(7);
        
        try {
            // 验证token
            if (!jwtService.validateToken(jwt)) {
                log.debug("JWT token验证失败");
                filterChain.doFilter(request, response);
                return;
            }

            // 从token中提取用户信息
            Long userId = jwtService.getUserIdFromToken(jwt);
            Long tenantId = jwtService.getTenantIdFromToken(jwt);
            
            System.out.println("=== DEBUG JwtAuthenticationFilter ===");
            System.out.println("从JWT解析的userId: " + userId);
            System.out.println("从JWT解析的tenantId: " + tenantId);
            
            // 设置租户上下文
            TenantContext.setCurrentTenantId(tenantId);
            System.out.println("已设置TenantContext.tenantId: " + tenantId);
            System.out.println("验证TenantContext.getCurrentTenantId(): " + TenantContext.getCurrentTenantId());
            
            // 如果用户未认证，则进行认证
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详情
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId.toString());
                
                // 创建认证token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 设置认证信息到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("用户 {} (租户: {}) 认证成功", userId, tenantId);
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage());
            // 清理租户上下文
            TenantContext.clear();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 清理租户上下文
        TenantContext.clear();
        super.destroy();
    }
}