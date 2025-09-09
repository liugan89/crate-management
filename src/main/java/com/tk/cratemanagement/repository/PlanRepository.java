package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Plan entity.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    
    /**
     * 根据计划名称查找订阅计划
     */
    Optional<Plan> findByName(String name);
}
