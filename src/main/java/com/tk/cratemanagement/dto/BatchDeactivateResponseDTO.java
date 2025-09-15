package com.tk.cratemanagement.dto;

/**
 * 批量周转筐报废响应DTO
 * 返回批量操作的结果统计
 */
public record BatchDeactivateResponseDTO(
        /**
         * 请求报废的总数量
         */
        int totalRequested,

        /**
         * 成功报废的数量
         */
        int successCount,

        /**
         * 失败的数量
         */
        int failCount,

        /**
         * 操作结果消息
         */
        String message
) {
    /**
     * 创建成功响应
     */
    public static BatchDeactivateResponseDTO success(int totalRequested, int successCount) {
        int failCount = totalRequested - successCount;
        String message = failCount == 0 
            ? String.format("批量报废成功：共处理 %d 个周转筐", successCount)
            : String.format("批量报废完成：成功 %d 个，失败 %d 个", successCount, failCount);
        
        return new BatchDeactivateResponseDTO(totalRequested, successCount, failCount, message);
    }
}