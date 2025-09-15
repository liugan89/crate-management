package com.tk.cratemanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 批量周转筐注册请求DTO
 * 用于批量注册多个周转筐
 */
public record BatchRegisterCratesRequestDTO(
        /**
         * 要注册的周转筐列表
         */
        @NotEmpty(message = "周转筐列表不能为空")
        @Size(max = 100, message = "单次最多只能注册100个周转筐")
        @Valid
        List<CrateRequestDTO> crates,

        /**
         * 批量操作备注（可选）
         */
        @Size(max = 500, message = "备注不能超过500个字符")
        String notes
) {
}