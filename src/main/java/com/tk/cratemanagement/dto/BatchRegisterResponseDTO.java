package com.tk.cratemanagement.dto;

import java.util.List;

/**
 * 批量周转筐注册响应DTO
 * 返回批量注册操作的结果
 */
public record BatchRegisterResponseDTO(
        /**
         * 请求注册的总数量
         */
        int totalRequested,

        /**
         * 成功注册的数量
         */
        int successCount,

        /**
         * 失败的数量
         */
        int failCount,

        /**
         * 成功注册的周转筐列表
         */
        List<CrateDTO> successfulCrates,

        /**
         * 失败的详细信息
         */
        List<BatchRegisterFailureDTO> failures,

        /**
         * 操作结果消息
         */
        String message
) {
    /**
     * 创建成功响应
     */
    public static BatchRegisterResponseDTO success(
            int totalRequested, 
            List<CrateDTO> successfulCrates, 
            List<BatchRegisterFailureDTO> failures) {
        
        int successCount = successfulCrates.size();
        int failCount = failures.size();
        
        String message = failCount == 0 
            ? String.format("批量注册成功：共注册 %d 个周转筐", successCount)
            : String.format("批量注册完成：成功 %d 个，失败 %d 个", successCount, failCount);
        
        return new BatchRegisterResponseDTO(
                totalRequested, 
                successCount, 
                failCount, 
                successfulCrates, 
                failures, 
                message
        );
    }

    /**
     * 批量注册失败详情
     */
    public record BatchRegisterFailureDTO(
            /**
             * 失败的NFC UID
             */
            String nfcUid,

            /**
             * 失败原因
             */
            String reason,

            /**
             * 错误代码（可选）
             */
            String errorCode
    ) {
        public static BatchRegisterFailureDTO of(String nfcUid, String reason) {
            return new BatchRegisterFailureDTO(nfcUid, reason, null);
        }

        public static BatchRegisterFailureDTO of(String nfcUid, String reason, String errorCode) {
            return new BatchRegisterFailureDTO(nfcUid, reason, errorCode);
        }
    }
}