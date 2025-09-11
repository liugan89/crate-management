package com.tk.cratemanagement.dto;

/**
 * 单位信息DTO
 * 用于返回商品单位的相关信息
 */
public record UnitDTO(
        String name,        // 枚举名称
        String code,        // 单位代码
        String displayName, // 显示名称
        String category     // 单位分类
) {}
