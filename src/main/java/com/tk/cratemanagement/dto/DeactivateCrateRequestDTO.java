package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.Size;

/**
 * 周转筐报废请求DTO
 * 用于单个周转筐报废操作
 */
public record DeactivateCrateRequestDTO(
        /**
         * 报废原因（可选）
         * 例如：损坏、丢失、老化等
         */
        @Size(max = 500, message = "报废原因不能超过500个字符")
        String reason
) {
}