package com.tk.cratemanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 批量周转筐报废请求DTO
 * 用于批量周转筐报废操作
 */
public record BatchDeactivateCratesRequestDTO(
        /**
         * 要报废的周转筐ID列表
         */
        @NotEmpty(message = "周转筐ID列表不能为空")
        List<Long> crateIds,

        /**
         * 报废原因（可选）
         * 例如：批量损坏、批量丢失、批量老化等
         */
        @Size(max = 500, message = "报废原因不能超过500个字符")
        String reason
) {
}