package com.tk.cratemanagement.service;

import com.tk.cratemanagement.dto.SyncRequestDTO;
import com.tk.cratemanagement.dto.SyncResponseDTO;

/**
 * 离线同步服务接口
 * 处理移动端离线操作的批量同步
 */
public interface SyncService {

    /**
     * 处理离线同步请求
     * 批量处理移动端提交的离线操作
     *
     * @param request 同步请求，包含操作列表
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return 同步响应，包含处理结果
     */
    SyncResponseDTO processSync(SyncRequestDTO request, Long tenantId, Long userId);
}