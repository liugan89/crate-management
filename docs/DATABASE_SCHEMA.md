# Database Schema (PostgreSQL)

This document provides the final DDL for the application's database schema. It is the source of truth for generating JPA Entities.

```sql
-- Tenancy, Users & Auth
CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TRIAL', -- 枚举: TRIAL, ACTIVE, SUSPENDED
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- 枚举: ADMIN, OPERATOR
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE (tenant_id, email)
);

-- Subscription & Billing
CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price_monthly DECIMAL(10, 2),
    quotas JSONB
);

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE REFERENCES tenants(id) ON DELETE CASCADE,
    plan_id INT NOT NULL REFERENCES plans(id),
    status VARCHAR(50) NOT NULL, -- TRIALING, ACTIVE, PAST_DUE, CANCELED
    current_period_end TIMESTAMPTZ NOT NULL,
    stripe_customer_id VARCHAR(255) UNIQUE,
    stripe_subscription_id VARCHAR(255) UNIQUE
);

CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, PAID, FAILED
    paid_at TIMESTAMPTZ,
    invoice_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE usage_snapshots (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    snapshot_date DATE NOT NULL,
    user_count INT NOT NULL,
    crate_count INT NOT NULL,
    UNIQUE (tenant_id, snapshot_date)
);

-- Master Data
CREATE TABLE goods (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    UNIQUE(tenant_id, sku)
);
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL
);
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL
);

-- Crate Management
CREATE TABLE crate_types (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    capacity DECIMAL(10, 2),
    weight DECIMAL(10, 2)
);

CREATE TABLE crates (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    nfc_uid VARCHAR(255) NOT NULL,
    crate_type_id BIGINT REFERENCES crate_types(id),
    status VARCHAR(50) NOT NULL, -- 枚举: AVAILABLE, IN_USE, OUTBOUND, INACTIVE
    UNIQUE (tenant_id, nfc_uid)
);

-- Core Warehouse Operations
CREATE TABLE shipment_orders (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    order_number VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- 枚举: INBOUND, OUTBOUND, INBOUND_ADJUSTMENT, OUTBOUND_ADJUSTMENT
    status VARCHAR(50) NOT NULL, -- 枚举: PENDING, IN_PROGRESS, COMPLETED, CANCELED
    notes TEXT,
    original_order_id BIGINT REFERENCES shipment_orders(id), -- 用于调整单
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(tenant_id, order_number)
);

CREATE TABLE shipment_order_items (
    id BIGSERIAL PRIMARY KEY,
    shipment_order_id BIGINT NOT NULL REFERENCES shipment_orders(id) ON DELETE CASCADE,
    goods_id BIGINT NOT NULL REFERENCES goods(id),
    supplier_id BIGINT REFERENCES suppliers(id),
    expected_quantity DECIMAL(10, 2),
    batch_number VARCHAR(100),
    production_date DATE
);

CREATE TABLE shipment_order_item_scans (
    id BIGSERIAL PRIMARY KEY,
    order_item_id BIGINT NOT NULL REFERENCES shipment_order_items(id) ON DELETE CASCADE,
    crate_id BIGINT NOT NULL REFERENCES crates(id),
    actual_quantity DECIMAL(10, 2) NOT NULL,
    scanned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    scanned_by_user_id BIGINT NOT NULL REFERENCES users(id)
);

-- Inventory Snapshot & Business Logs
CREATE TABLE crate_contents (
    id BIGSERIAL PRIMARY KEY,
    crate_id BIGINT NOT NULL UNIQUE REFERENCES crates(id) ON DELETE CASCADE,
    tenant_id BIGINT NOT NULL,
    goods_id BIGINT,
    supplier_id BIGINT,
    batch_number VARCHAR(100),
    quantity DECIMAL(10, 2),
    status VARCHAR(50) NOT NULL, -- 枚举: AVAILABLE, INBOUND, OUTBOUND
    last_updated_at TIMESTAMPTZ NOT NULL,
    last_updated_by_order_id BIGINT REFERENCES shipment_orders(id)
);

CREATE TABLE operation_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);