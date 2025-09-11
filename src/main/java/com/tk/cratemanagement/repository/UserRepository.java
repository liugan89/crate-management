package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据租户ID和邮箱查找用户（排除软删除）
     */
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findOneByTenantIdAndEmail(@Param("tenantId") Long tenantId, @Param("email") String email);
    
    /**
     * 根据邮箱查找用户（跨租户，用于登录，排除软删除）
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 根据租户ID查找所有用户（排除软删除）
     */
    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.deletedAt IS NULL")
    List<User> findByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 根据用户ID和租户ID查找用户（排除软删除）
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.tenantId = :tenantId AND u.deletedAt IS NULL")
    Optional<User> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * 统计租户下的用户数量（排除软删除）
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.deletedAt IS NULL")
    int countByTenantId(@Param("tenantId") Long tenantId);
}
