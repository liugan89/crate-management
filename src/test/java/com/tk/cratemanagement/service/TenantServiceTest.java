package com.tk.cratemanagement.service;

import com.tk.cratemanagement.config.TenantContext;
import com.tk.cratemanagement.domain.Tenant;
import com.tk.cratemanagement.domain.enumeration.TenantStatus;
import com.tk.cratemanagement.dto.TenantDTO;
import com.tk.cratemanagement.dto.TenantUpdateDTO;
import com.tk.cratemanagement.repository.TenantRepository;
import com.tk.cratemanagement.service.impl.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setCompanyName("测试公司");
        testTenant.setContactEmail("contact@test.com");
        testTenant.setPhoneNumber("+86-400-123-4567");
        testTenant.setAddress("测试地址");
        testTenant.setCity("北京");
        testTenant.setState("北京市");
        testTenant.setZipCode("100000");
        testTenant.setCountry("CN");
        testTenant.setTimezone("Asia/Shanghai");
        testTenant.setStatus(TenantStatus.TRIAL);
        testTenant.setCreatedAt(LocalDateTime.now());
        testTenant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetTenantById() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));

        // When
        TenantDTO result = tenantService.getTenantById(1L);

        // Then
        assertNotNull(result);
        assertEquals("测试公司", result.companyName());
        assertEquals("contact@test.com", result.contactEmail());
        assertEquals(TenantStatus.TRIAL, result.status());
    }

    @Test
    void testUpdateTenantStatus() {
        // Given
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);

        // When
        TenantDTO result = tenantService.updateTenantStatus(1L, TenantStatus.ACTIVE);

        // Then
        assertNotNull(result);
        verify(tenantRepository).save(testTenant);
        assertEquals(TenantStatus.ACTIVE, testTenant.getStatus());
    }

    @Test
    void testUpdateCurrentTenant() {
        // Given
        TenantContext.setCurrentTenantId(1L);
        TenantUpdateDTO updateDTO = new TenantUpdateDTO(
            "更新后的公司名称",
            "new-contact@test.com",
            "+86-400-999-8888",
            "新地址",
            "上海",
            "上海市",
            "200000",
            "CN",
            "Asia/Shanghai"
        );
        
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(testTenant));
        when(tenantRepository.existsByCompanyNameAndIdNot("更新后的公司名称", 1L)).thenReturn(false);
        when(tenantRepository.existsByContactEmailAndIdNot("new-contact@test.com", 1L)).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);

        // When
        TenantDTO result = tenantService.updateCurrentTenant(updateDTO);

        // Then
        assertNotNull(result);
        verify(tenantRepository).save(testTenant);
        assertEquals("更新后的公司名称", testTenant.getCompanyName());
        assertEquals("new-contact@test.com", testTenant.getContactEmail());
        
        // Cleanup
        TenantContext.clear();
    }
}