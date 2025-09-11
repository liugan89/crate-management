# Database Schema (PostgreSQL)

This document provides the optimized DDL for the application's database schema. This version incorporates soft-delete flags, expanded attributes, performance indexes, and additional enterprise features.

```sql
-- ==============================================
-- 1. 租户和用户管理 (Tenancy, Users & Auth)
-- ==============================================

CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    phone_number VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(50),
    country VARCHAR(100) DEFAULT 'CN', -- 新增：国家字段
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai', -- 新增：时区
    status VARCHAR(50) NOT NULL DEFAULT 'TRIAL', -- 枚举: TRIAL, ACTIVE, SUSPENDED
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), -- 新增：更新时间
    deleted_at TIMESTAMPTZ DEFAULT NULL -- Soft delete flag
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(50), -- 新增：手机号
    avatar_url TEXT, -- 新增：头像URL
    role VARCHAR(50) NOT NULL, -- 枚举: ADMIN, OPERATOR
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMPTZ, -- 新增：最后登录时间
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(), -- 新增：创建时间
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), -- 新增：更新时间
    deleted_at TIMESTAMPTZ DEFAULT NULL, -- Soft delete flag
    UNIQUE (tenant_id, email)
);

-- ==============================================
-- 2. 订阅和计费 (Subscription & Billing)
-- ==============================================

CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT, -- 新增：计划描述
    price_monthly DECIMAL(10, 2),
    price_yearly DECIMAL(10, 2), -- 新增：年费价格
    quotas JSONB,
    features JSONB, -- 新增：功能特性列表
    is_active BOOLEAN DEFAULT TRUE, -- 新增：是否激活
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    plan_id INT NOT NULL REFERENCES plans(id),
    status VARCHAR(50) NOT NULL, -- TRIALING, ACTIVE, PAST_DUE, CANCELED
    current_period_start TIMESTAMPTZ NOT NULL, -- 新增：当前周期开始时间
    current_period_end TIMESTAMPTZ NOT NULL,
    trial_end TIMESTAMPTZ, -- 新增：试用结束时间
    stripe_customer_id VARCHAR(255) UNIQUE,
    stripe_subscription_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL -- Soft delete flag
);

CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id),
    invoice_number VARCHAR(100) NOT NULL UNIQUE, -- 新增：发票编号
    amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0, -- 新增：税费
    total_amount DECIMAL(10, 2) NOT NULL, -- 新增：总金额
    currency VARCHAR(3) DEFAULT 'CNY', -- 新增：货币
    status VARCHAR(50) NOT NULL, -- PENDING, PAID, FAILED, REFUNDED
    due_date TIMESTAMPTZ, -- 新增：到期日期
    paid_at TIMESTAMPTZ,
    invoice_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL -- Soft delete flag
);

-- ==============================================
-- 3. 分析和用量统计 (Analytics & Usage)
-- ==============================================

CREATE TABLE usage_snapshots (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    snapshot_date DATE NOT NULL,
    user_count INT NOT NULL,
    crate_count INT NOT NULL,
    active_crate_count INT NOT NULL DEFAULT 0, -- 新增：活跃周转筐数
    order_count INT NOT NULL DEFAULT 0, -- 新增：订单数
    scan_count INT NOT NULL DEFAULT 0, -- 新增：扫码次数
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, snapshot_date)
);

-- ==============================================
-- 4. 主数据管理 (Master Data)
-- ==============================================

CREATE TABLE goods (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    barcode VARCHAR(100), -- 新增：条形码
    unit VARCHAR(50), -- e.g., 'kg', 'box', 'piece'
    category VARCHAR(100), -- 新增：分类
    image_url TEXT,
    description TEXT,
    custom_fields JSONB,
    is_active BOOLEAN DEFAULT TRUE, -- 新增：是否激活
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE(tenant_id, sku)
);

CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100), -- 新增：供应商编码
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    address TEXT,
    city VARCHAR(100), -- 新增：城市
    state VARCHAR(100), -- 新增：省份
    zip_code VARCHAR(50), -- 新增：邮编
    country VARCHAR(100) DEFAULT 'CN', -- 新增：国家
    is_active BOOLEAN DEFAULT TRUE, -- 新增：是否激活
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (tenant_id, name),
    UNIQUE (tenant_id, code) -- 新增：编码唯一性约束
);

CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100), -- 新增：库位编码
    description TEXT, -- 新增：描述
    zone VARCHAR(100), -- 新增：区域
    is_active BOOLEAN DEFAULT TRUE, -- 新增：是否激活
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE(tenant_id, name),
    UNIQUE(tenant_id, code) -- 新增：编码唯一性约束
);

CREATE TABLE crate_types (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100), -- 新增：类型编码
    capacity DECIMAL(10, 2),
    weight DECIMAL(10, 2),
    dimensions VARCHAR(100), -- e.g., '50x30x25 cm'
    material VARCHAR(100),
    color VARCHAR(50), -- 新增：颜色
    is_active BOOLEAN DEFAULT TRUE, -- 新增：是否激活
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (tenant_id, name),
    UNIQUE (tenant_id, code) -- 新增：编码唯一性约束
);

-- ==============================================
-- 5. 周转筐管理 (Crate Management)
-- ==============================================

CREATE TABLE crates (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nfc_uid VARCHAR(255) NOT NULL,
    crate_type_id BIGINT REFERENCES crate_types(id),
    status VARCHAR(50) NOT NULL, -- 枚举: AVAILABLE, IN_USE, OUTBOUND, INACTIVE, MAINTENANCE
    last_known_location_id BIGINT REFERENCES locations(id),
    last_seen_at TIMESTAMPTZ,
    maintenance_due_date DATE, -- 新增：维护到期日期
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (tenant_id, nfc_uid)
);

-- ==============================================
-- 6. 核心仓储操作 (Core Warehouse Operations)
-- ==============================================

CREATE TABLE shipment_orders (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    order_number VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 枚举: INBOUND, OUTBOUND, ADJUSTMENT
    status VARCHAR(50) NOT NULL, -- 枚举: PENDING, IN_PROGRESS, COMPLETED, CANCELED
    priority VARCHAR(20) DEFAULT 'NORMAL', -- 新增：优先级 (LOW, NORMAL, HIGH, URGENT)
    notes TEXT,
    expected_delivery_date TIMESTAMPTZ, -- 新增：预期交付日期
    actual_delivery_date TIMESTAMPTZ, -- 新增：实际交付日期
    created_by_user_id BIGINT REFERENCES users(id), -- 新增：创建人
    completed_by_user_id BIGINT REFERENCES users(id), -- 新增：完成人
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ, -- 新增：完成时间
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE(tenant_id, order_number)
);

CREATE TABLE shipment_order_items (
    id BIGSERIAL PRIMARY KEY,
    shipment_order_id BIGINT NOT NULL REFERENCES shipment_orders(id) ON DELETE CASCADE,
    goods_id BIGINT NOT NULL REFERENCES goods(id),
    supplier_id BIGINT REFERENCES suppliers(id),
    expected_quantity DECIMAL(10, 2),
    actual_quantity DECIMAL(10, 2) DEFAULT 0, -- 新增：实际数量汇总
    batch_number VARCHAR(100),
    production_date DATE,
    expiry_date DATE, -- 新增：过期日期
    unit_price DECIMAL(10, 2), -- 新增：单价
    total_price DECIMAL(10, 2), -- 新增：总价
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE shipment_order_item_scans (
    id BIGSERIAL PRIMARY KEY,
    order_item_id BIGINT NOT NULL REFERENCES shipment_order_items(id) ON DELETE CASCADE,
    crate_id BIGINT NOT NULL REFERENCES crates(id),
    actual_quantity DECIMAL(10, 2) NOT NULL,
    scanned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    scanned_by_user_id BIGINT NOT NULL REFERENCES users(id),
    location_id BIGINT REFERENCES locations(id), -- 新增：扫码位置
    device_info JSONB, -- 新增：设备信息
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ==============================================
-- 7. 库存快照和业务日志 (Inventory Snapshot & Business Logs)
-- ==============================================

CREATE TABLE crate_contents (
    id BIGSERIAL PRIMARY KEY,
    crate_id BIGINT NOT NULL UNIQUE REFERENCES crates(id) ON DELETE CASCADE,
    tenant_id BIGINT NOT NULL,
    goods_id BIGINT,
    supplier_id BIGINT,
    batch_number VARCHAR(100),
    quantity DECIMAL(10, 2),
    status VARCHAR(50) NOT NULL, -- 枚举: AVAILABLE, INBOUND, OUTBOUND
    location_id BIGINT REFERENCES locations(id), -- 新增：当前位置
    last_updated_at TIMESTAMPTZ NOT NULL,
    last_updated_by_order_id BIGINT REFERENCES shipment_orders(id),
    last_updated_by_user_id BIGINT REFERENCES users(id) -- 新增：最后更新用户
);

CREATE TABLE operation_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    description TEXT, -- 新增：操作描述
    payload JSONB,
    ip_address INET, -- 新增：IP地址
    user_agent TEXT, -- 新增：用户代理
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ==============================================
-- 8. 性能优化索引 (Performance Indexes)
-- ==============================================

-- 租户相关索引
CREATE INDEX idx_tenants_status ON tenants(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_tenants_created_at ON tenants(created_at);

-- 用户相关索引
CREATE INDEX idx_users_tenant_id ON users(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role) WHERE deleted_at IS NULL;

-- 主数据索引
CREATE INDEX idx_goods_tenant_id ON goods(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_goods_sku ON goods(sku) WHERE deleted_at IS NULL;
CREATE INDEX idx_suppliers_tenant_id ON suppliers(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_locations_tenant_id ON locations(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_crate_types_tenant_id ON crate_types(tenant_id) WHERE deleted_at IS NULL;

-- 周转筐相关索引
CREATE INDEX idx_crates_tenant_id ON crates(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_crates_nfc_uid ON crates(nfc_uid);
CREATE INDEX idx_crates_status ON crates(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_crates_last_known_location ON crates(last_known_location_id);

-- 仓储操作索引
CREATE INDEX idx_shipment_orders_tenant_id ON shipment_orders(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_shipment_orders_status ON shipment_orders(status);
CREATE INDEX idx_shipment_orders_type ON shipment_orders(type);
CREATE INDEX idx_shipment_orders_created_at ON shipment_orders(created_at);
CREATE INDEX idx_shipment_order_items_order_id ON shipment_order_items(shipment_order_id);
CREATE INDEX idx_shipment_order_items_goods_id ON shipment_order_items(goods_id);
CREATE INDEX idx_scans_order_item_id ON shipment_order_item_scans(order_item_id);
CREATE INDEX idx_scans_crate_id ON shipment_order_item_scans(crate_id);
CREATE INDEX idx_scans_scanned_at ON shipment_order_item_scans(scanned_at);

-- 库存相关索引
CREATE INDEX idx_crate_contents_tenant_id ON crate_contents(tenant_id);
CREATE INDEX idx_crate_contents_goods_id ON crate_contents(goods_id);
CREATE INDEX idx_crate_contents_status ON crate_contents(status);
CREATE INDEX idx_crate_contents_location_id ON crate_contents(location_id);

-- 操作日志索引
CREATE INDEX idx_operation_logs_tenant_id ON operation_logs(tenant_id);
CREATE INDEX idx_operation_logs_user_id ON operation_logs(user_id);
CREATE INDEX idx_operation_logs_entity ON operation_logs(entity_type, entity_id);
CREATE INDEX idx_operation_logs_created_at ON operation_logs(created_at);

-- ==============================================
-- 9. 触发器 (Triggers) - 自动更新时间戳
-- ==============================================

-- 创建更新时间戳函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为相关表添加更新时间戳触发器
CREATE TRIGGER update_tenants_updated_at BEFORE UPDATE ON tenants FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscriptions_updated_at BEFORE UPDATE ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_goods_updated_at BEFORE UPDATE ON goods FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_suppliers_updated_at BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_locations_updated_at BEFORE UPDATE ON locations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_crate_types_updated_at BEFORE UPDATE ON crate_types FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_crates_updated_at BEFORE UPDATE ON crates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shipment_orders_updated_at BEFORE UPDATE ON shipment_orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shipment_order_items_updated_at BEFORE UPDATE ON shipment_order_items FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ==============================================
-- 10. 视图 (Views) - 常用查询视图
-- ==============================================

-- 活跃库存视图
CREATE VIEW v_active_inventory AS
SELECT 
    cc.tenant_id,
    cc.goods_id,
    g.name as goods_name,
    g.sku,
    g.unit,
    cc.supplier_id,
    s.name as supplier_name,
    cc.batch_number,
    SUM(cc.quantity) as total_quantity,
    COUNT(cc.crate_id) as crate_count,
    cc.status
FROM crate_contents cc
JOIN goods g ON cc.goods_id = g.id
LEFT JOIN suppliers s ON cc.supplier_id = s.id
WHERE cc.status = 'INBOUND' 
  AND cc.tenant_id = g.tenant_id
  AND (s.id IS NULL OR cc.tenant_id = s.tenant_id)
GROUP BY cc.tenant_id, cc.goods_id, g.name, g.sku, g.unit, cc.supplier_id, s.name, cc.batch_number, cc.status;

-- 周转筐状态统计视图
CREATE VIEW v_crate_status_summary AS
SELECT 
    tenant_id,
    status,
    COUNT(*) as count,
    COUNT(*) FILTER (WHERE last_seen_at > now() - INTERVAL '7 days') as active_last_7_days
FROM crates 
WHERE deleted_at IS NULL
GROUP BY tenant_id, status;

-- ==============================================
-- 11. 约束和检查 (Constraints & Checks)
-- ==============================================

-- 添加检查约束
ALTER TABLE tenants ADD CONSTRAINT chk_tenant_status CHECK (status IN ('TRIAL', 'ACTIVE', 'SUSPENDED'));
ALTER TABLE users ADD CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'OPERATOR'));
ALTER TABLE subscriptions ADD CONSTRAINT chk_subscription_status CHECK (status IN ('TRIALING', 'ACTIVE', 'PAST_DUE', 'CANCELED'));
ALTER TABLE invoices ADD CONSTRAINT chk_invoice_status CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED'));
ALTER TABLE crates ADD CONSTRAINT chk_crate_status CHECK (status IN ('AVAILABLE', 'IN_USE', 'OUTBOUND', 'INACTIVE', 'MAINTENANCE'));
ALTER TABLE shipment_orders ADD CONSTRAINT chk_order_type CHECK (type IN ('INBOUND', 'OUTBOUND', 'ADJUSTMENT'));
ALTER TABLE shipment_orders ADD CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELED'));
ALTER TABLE shipment_orders ADD CONSTRAINT chk_order_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'));
ALTER TABLE crate_contents ADD CONSTRAINT chk_content_status CHECK (status IN ('AVAILABLE', 'INBOUND', 'OUTBOUND'));

-- 添加非负值约束
ALTER TABLE goods ADD CONSTRAINT chk_goods_positive CHECK (id > 0);
ALTER TABLE suppliers ADD CONSTRAINT chk_suppliers_positive CHECK (id > 0);
ALTER TABLE locations ADD CONSTRAINT chk_locations_positive CHECK (id > 0);
ALTER TABLE crate_types ADD CONSTRAINT chk_crate_types_positive CHECK (id > 0);
ALTER TABLE crates ADD CONSTRAINT chk_crates_positive CHECK (id > 0);
ALTER TABLE shipment_orders ADD CONSTRAINT chk_orders_positive CHECK (id > 0);
ALTER TABLE shipment_order_items ADD CONSTRAINT chk_items_positive CHECK (id > 0);
ALTER TABLE shipment_order_item_scans ADD CONSTRAINT chk_scans_positive CHECK (id > 0);
ALTER TABLE crate_contents ADD CONSTRAINT chk_contents_positive CHECK (id > 0);
ALTER TABLE operation_logs ADD CONSTRAINT chk_logs_positive CHECK (id > 0);
```

## 主要优化点总结

### 🚀 新增功能
1. **完整的审计追踪** - 添加了created_at、updated_at字段
2. **国际化支持** - 添加了时区、货币、国家字段
3. **业务增强** - 优先级、维护提醒、设备信息等
4. **性能优化** - 全面的索引策略
5. **数据完整性** - 检查约束和触发器

### 📊 性能优化
1. **索引策略** - 为所有常用查询字段添加索引
2. **视图支持** - 预定义常用查询视图
3. **触发器** - 自动维护更新时间戳

### 🔒 数据安全
1. **约束检查** - 确保数据完整性
2. **软删除** - 支持数据恢复
3. **审计日志** - 完整的操作追踪

这个优化版本在保持您原有优秀设计的基础上，进一步增强了企业级应用所需的完整性、性能和可维护性。