package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.Plan;
import com.tk.cratemanagement.domain.Subscription;
import com.tk.cratemanagement.domain.Tenant;
import com.tk.cratemanagement.domain.User;
import com.tk.cratemanagement.domain.enumeration.SubscriptionStatus;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;
import com.tk.cratemanagement.domain.enumeration.UserRole;
import com.tk.cratemanagement.dto.AuthResponseDTO;
import com.tk.cratemanagement.dto.LoginRequestDTO;
import com.tk.cratemanagement.dto.RegisterRequestDTO;
import com.tk.cratemanagement.repository.PlanRepository;
import com.tk.cratemanagement.repository.SubscriptionRepository;
import com.tk.cratemanagement.repository.TenantRepository;
import com.tk.cratemanagement.repository.UserRepository;
import com.tk.cratemanagement.security.CustomUserDetailsService;
import com.tk.cratemanagement.service.AuthService;
import com.tk.cratemanagement.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 认证服务实现类
 * 实现用户认证和租户注册的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 注册新租户和管理员用户
     * 这是一个原子性操作，在单个事务中完成：
     * 1. 创建租户
     * 2. 创建ADMIN角色用户
     * 3. 创建14天试用订阅
     */
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("开始注册新租户: {}", request.companyName());

        // 检查邮箱是否已存在（跨租户检查）
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("管理员邮箱已被使用");
        }
        
        // 检查公司名称是否已存在
        if (tenantRepository.findByCompanyName(request.companyName()).isPresent()) {
            throw new IllegalArgumentException("公司名称已被使用");
        }
        
        // 检查联系邮箱是否已存在（如果提供了的话）
        if (request.contactEmail() != null && !request.contactEmail().trim().isEmpty()) {
            if (tenantRepository.findByContactEmail(request.contactEmail()).isPresent()) {
                throw new IllegalArgumentException("联系邮箱已被使用");
            }
        }

        // 1. 创建租户
        Tenant tenant = new Tenant();
        tenant.setCompanyName(request.companyName());
        tenant.setContactEmail(request.contactEmail());
        tenant.setPhoneNumber(request.phoneNumber());
        tenant.setAddress(request.address());
        tenant.setCity(request.city());
        tenant.setState(request.state());
        tenant.setZipCode(request.zipCode());
        
        // 设置国家和时区，如果未提供则使用默认值
        if (request.country() != null && !request.country().trim().isEmpty()) {
            tenant.setCountry(request.country());
        }
        if (request.timezone() != null && !request.timezone().trim().isEmpty()) {
            tenant.setTimezone(request.timezone());
        }
        
        // status和createdAt字段都有默认值，无需手动设置
        tenant = tenantRepository.save(tenant);

        // 2. 创建管理员用户
        User adminUser = new User();
        adminUser.setTenantId(tenant.getId());
        adminUser.setEmail(request.email());
        adminUser.setPasswordHash(passwordEncoder.encode(request.password()));
        adminUser.setFullName(request.fullName());
        adminUser.setPhone(request.phone());
        adminUser.setRole(UserRole.ADMIN);
        // isActive字段有默认值true，无需手动设置
        adminUser = userRepository.save(adminUser);

        // 3. 创建14天试用订阅
        Plan trialPlan = planRepository.findByName("TRIAL")
                .orElseThrow(() -> new IllegalStateException("试用计划不存在"));

        Subscription subscription = new Subscription();
        subscription.setTenantId(tenant.getId());
        subscription.setPlan(trialPlan);
        subscription.setStatus(SubscriptionStatus.TRIALING);
        subscription.setCurrentPeriodEnd(Instant.now().plus(14, ChronoUnit.DAYS));
        subscriptionRepository.save(subscription);

        // 生成JWT token
        String token = jwtService.generateToken(adminUser.getId(), tenant.getId(), adminUser.getRole());

        log.info("租户注册成功: tenantId={}, userId={}", tenant.getId(), adminUser.getId());
        return new AuthResponseDTO(tenant.getId(), token);
    }

    /**
     * 用户登录认证
     * 验证用户凭据并返回JWT token
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("用户登录尝试: {}", request.email());

        try {
            // 使用UserDetailsService加载用户并验证凭据
            UserDetails userDetails = userDetailsService.loadUserByEmail(request.email());
            
            // 验证密码
            if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
            }

            // 获取用户信息用于生成token
            Long userId = Long.parseLong(userDetails.getUsername());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("用户不存在"));

            // 检查租户状态
            Tenant tenant = tenantRepository.findById(user.getTenantId())
                    .orElseThrow(() -> new IllegalStateException("租户不存在"));

            if (tenant.getStatus() == TenantStatus.SUSPENDED) {
                throw new BadCredentialsException("租户账户已被暂停");
            }

            // 生成JWT token
            String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getRole());

            log.info("用户登录成功: userId={}, tenantId={}", user.getId(), user.getTenantId());
            return new AuthResponseDTO(user.getTenantId(), token);
            
        } catch (Exception e) {
            log.warn("用户登录失败: {}, 原因: {}", request.email(), e.getMessage());
            throw new BadCredentialsException("用户名或密码错误");
        }
    }
}