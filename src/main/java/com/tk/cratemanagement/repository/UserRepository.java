package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findOneByTenantIdAndEmail(Long tenantId, String email);
    
    /**
     * 根据邮箱查找用户（跨租户，用于登录）
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据租户ID查找所有用户
     */
    List<User> findByTenantId(Long tenantId);
    
    /**
     * 根据用户ID和租户ID查找用户
     */
    Optional<User> findByIdAndTenantId(Long id, Long tenantId);
    
    /**
     * 统计租户下的用户数量
     */
    int countByTenantId(Long tenantId);
}
