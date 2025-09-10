package com.tk.cratemanagement.service.impl;

import com.tk.cratemanagement.domain.enumeration.UserRole;
import com.tk.cratemanagement.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT服务实现类
 * 处理JWT token的生成、验证和解析
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final long jwtExpirationInHours;

    public JwtServiceImpl(@Value("${app.jwt.secret:mySecretKey}") String secret,
                         @Value("${app.jwt.expiration-hours:24}") long jwtExpirationInHours) {
        // 确保密钥长度至少256位
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // 如果密钥太短，重复填充到32字节
            byte[] paddedKey = new byte[32];
            for (int i = 0; i < 32; i++) {
                paddedKey[i] = keyBytes[i % keyBytes.length];
            }
            this.secretKey = Keys.hmacShaKeyFor(paddedKey);
        } else {
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.jwtExpirationInHours = jwtExpirationInHours;
        log.info("JWT服务初始化完成，密钥长度: {} 位", this.secretKey.getEncoded().length * 8);
    }

    @Override
    public String generateToken(Long userId, Long tenantId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("tenantId", tenantId);
        claims.put("role", role.name());

        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationInHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }

    @Override
    public Long getTenantIdFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("tenantId", Long.class);
    }

    @Override
    public UserRole getRoleFromToken(String token) {
        Claims claims = extractClaims(token);
        String roleStr = claims.get("role", String.class);
        return UserRole.valueOf(roleStr);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}