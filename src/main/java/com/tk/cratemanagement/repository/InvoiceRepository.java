package com.tk.cratemanagement.repository;

import com.tk.cratemanagement.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Invoice entity.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * 根据订阅ID查找发票，按创建时间倒序排列
     */
    List<Invoice> findBySubscriptionIdOrderByCreatedAtDesc(Long subscriptionId);
}
