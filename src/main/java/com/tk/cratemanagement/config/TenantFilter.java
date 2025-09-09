package com.tk.cratemanagement.config;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 * 多租户过滤器定义
 * 用于自动在JPA查询中添加tenant_id条件
 */
@FilterDef(
    name = "tenantFilter",
    parameters = @ParamDef(name = "tenantId", type = Long.class)
)
public class TenantFilter {
    // 这是一个标记类，用于定义Hibernate过滤器
    // 实际的过滤器逻辑由Hibernate自动处理
}
