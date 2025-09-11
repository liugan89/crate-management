# 前端JWT接口调用指南

## 📋 概述

本文档为前端工程师提供完整的JWT认证和API调用指南，包括认证流程、API调用方法、错误处理等。

## 🔐 认证流程

### 1. 用户注册

**接口**: `POST /api/v1/auth/register`

**请求参数** ⚠️ 密码长度至少 8 位。:
```json
{
  "email": "user@example.com",
  "password": "password123456", 
  "username": "username",
  "companyName": "公司名称"
}
```

**JavaScript示例**:
```javascript
const registerData = {
  email: "user@example.com",
  password: "password123456",
  username: "username",
  companyName: "公司名称"
};

const registerResponse = await fetch('http://localhost:8080/api/v1/auth/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(registerData)
});

const result = await registerResponse.json();
```

### 2. 用户登录获取JWT Token

**接口**: `POST /api/v1/auth/login`

**请求参数**:
```json
{
  "email": "user@example.com",
  "password": "password123456"
}
```

**响应**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "tenantId": 1,
  "role": "ADMIN"
}
```

**JavaScript示例**:
```javascript
const loginData = {
  email: "user@example.com",
  password: "password123456"
};

const loginResponse = await fetch('http://localhost:8080/api/v1/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(loginData)
});

const { token, userId, tenantId, role } = await loginResponse.json();

// 保存token到localStorage
localStorage.setItem('jwt_token', token);
localStorage.setItem('user_id', userId);
localStorage.setItem('tenant_id', tenantId);
localStorage.setItem('user_role', role);
```

## 🔧 API调用方法

### 请求头设置

所有受保护的API都需要以下请求头：

```javascript
const token = localStorage.getItem('jwt_token');
const tenantId = localStorage.getItem('tenant_id');

const headers = {
  'Authorization': `Bearer ${token}`,
  'X-Tenant-ID': tenantId,
  'Content-Type': 'application/json'
};
```

### 主要API接口

#### 1. 货物管理

**获取货物列表**
```javascript
// GET /api/v1/goods
const goodsResponse = await fetch('http://localhost:8080/api/v1/goods', {
  method: 'GET',
  headers: headers
});
const goods = await goodsResponse.json();
```

**创建货物**
```javascript
// POST /api/v1/goods
const goodsData = {
  name: "新商品",
  description: "商品描述",
  unit: "个",
  category: "分类"
};

const createResponse = await fetch('http://localhost:8080/api/v1/goods', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(goodsData)
});
const newGoods = await createResponse.json();
```

**更新货物**
```javascript
// PUT /api/v1/goods/{id}
const updateData = {
  name: "更新后的商品名",
  description: "更新后的描述"
};

const updateResponse = await fetch(`http://localhost:8080/api/v1/goods/${goodsId}`, {
  method: 'PUT',
  headers: headers,
  body: JSON.stringify(updateData)
});
```

**删除货物**
```javascript
// DELETE /api/v1/goods/{id}
const deleteResponse = await fetch(`http://localhost:8080/api/v1/goods/${goodsId}`, {
  method: 'DELETE',
  headers: headers
});
```

#### 2. 供应商管理

**获取供应商列表**
```javascript
// GET /api/v1/suppliers
const suppliersResponse = await fetch('http://localhost:8080/api/v1/suppliers', {
  method: 'GET',
  headers: headers
});
const suppliers = await suppliersResponse.json();
```

**创建供应商**
```javascript
// POST /api/v1/suppliers
const supplierData = {
  name: "供应商名称",
  contactPerson: "联系人",
  phone: "联系电话",
  email: "supplier@example.com",
  address: "供应商地址"
};

const createSupplierResponse = await fetch('http://localhost:8080/api/v1/suppliers', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(supplierData)
});
```

#### 3. 周转筐管理

**获取周转筐列表**
```javascript
// GET /api/v1/crates
const cratesResponse = await fetch('http://localhost:8080/api/v1/crates', {
  method: 'GET',
  headers: headers
});
const crates = await cratesResponse.json();
```

**注册周转筐**
```javascript
// POST /api/v1/crates
const crateData = {
  nfcUid: "NFC123456789",
  crateTypeId: 1,
  locationId: 1
};

const createCrateResponse = await fetch('http://localhost:8080/api/v1/crates', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(crateData)
});
```

#### 4. 仓储操作

**创建入库单据**
```javascript
// POST /api/v1/shipment-orders
const orderData = {
  type: "INBOUND",
  description: "入库单据描述",
  expectedDeliveryDate: "2024-12-31T23:59:59Z",
  items: [{
    goodsId: 1,
    supplierId: 1,
    batchNumber: "BATCH001",
    expectedQuantity: 100
  }]
};

const createOrderResponse = await fetch('http://localhost:8080/api/v1/shipment-orders', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(orderData)
});
const newOrder = await createOrderResponse.json();
```

**获取单据列表**
```javascript
// GET /api/v1/shipment-orders
const ordersResponse = await fetch('http://localhost:8080/api/v1/shipment-orders', {
  method: 'GET',
  headers: headers
});
const orders = await ordersResponse.json();
```

**添加扫码记录**
```javascript
// POST /api/v1/shipment-order-items/{itemId}/scans
const scanData = {
  nfcUid: "NFC123456789",
  actualQuantity: 50
};

const addScanResponse = await fetch(`http://localhost:8080/api/v1/shipment-order-items/${itemId}/scans`, {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(scanData)
});
```

**完成单据**
```javascript
// POST /api/v1/shipment-orders/{orderId}/complete
const completeResponse = await fetch(`http://localhost:8080/api/v1/shipment-orders/${orderId}/complete`, {
  method: 'POST',
  headers: headers
});
```

#### 5. 库存查询

**获取库存汇总**
```javascript
// GET /api/v1/inventory/summary
const summaryResponse = await fetch('http://localhost:8080/api/v1/inventory/summary', {
  method: 'GET',
  headers: headers
});
const summary = await summaryResponse.json();
```

**获取库存详情**
```javascript
// GET /api/v1/inventory/details?goods_id={goodsId}
const detailsResponse = await fetch(`http://localhost:8080/api/v1/inventory/details?goods_id=${goodsId}`, {
  method: 'GET',
  headers: headers
});
const details = await detailsResponse.json();
```

**获取周转筐历史**
```javascript
// GET /api/v1/history/crates?nfc_uid={nfcUid}
const historyResponse = await fetch(`http://localhost:8080/api/v1/history/crates?nfc_uid=${nfcUid}`, {
  method: 'GET',
  headers: headers
});
const history = await historyResponse.json();
```

#### 6. 同步功能

**获取同步状态**
```javascript
// GET /api/v1/sync/status
const syncStatusResponse = await fetch('http://localhost:8080/api/v1/sync/status', {
  method: 'GET',
  headers: headers
});
const syncStatus = await syncStatusResponse.json();
```

**离线同步**
```javascript
// POST /api/v1/sync
const syncData = {
  operations: [
    {
      type: "CREATE_GOODS",
      data: { name: "离线创建的商品" }
    }
  ]
};

const syncResponse = await fetch('http://localhost:8080/api/v1/sync', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(syncData)
});
```

## 🛠️ 完整的API客户端封装

```javascript
class CrateManagementAPI {
  constructor(baseURL = 'http://localhost:8080/api/v1') {
    this.baseURL = baseURL;
    this.token = localStorage.getItem('jwt_token');
    this.tenantId = localStorage.getItem('tenant_id');
  }

  setToken(token, tenantId) {
    this.token = token;
    this.tenantId = tenantId;
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('tenant_id', tenantId);
  }

  getHeaders() {
    return {
      'Authorization': `Bearer ${this.token}`,
      'X-Tenant-ID': this.tenantId,
      'Content-Type': 'application/json'
    };
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const response = await fetch(url, {
      ...options,
      headers: {
        ...this.getHeaders(),
        ...options.headers
      }
    });

    if (response.status === 401) {
      // Token过期，清除并跳转登录
      this.token = null;
      this.tenantId = null;
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('tenant_id');
      throw new Error('认证失败，请重新登录');
    }

    if (!response.ok) {
      throw new Error(`API请求失败: ${response.status} ${response.statusText}`);
    }

    return response;
  }

  // 认证相关
  async register(userData) {
    const response = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData)
    });
    return response.json();
  }

  async login(email, password) {
    const response = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();
    this.setToken(data.token, data.tenantId);
    return data;
  }

  // 货物管理
  async getGoods() {
    const response = await this.request('/goods');
    return response.json();
  }

  async createGoods(goodsData) {
    const response = await this.request('/goods', {
      method: 'POST',
      body: JSON.stringify(goodsData)
    });
    return response.json();
  }

  async updateGoods(id, goodsData) {
    const response = await this.request(`/goods/${id}`, {
      method: 'PUT',
      body: JSON.stringify(goodsData)
    });
    return response.json();
  }

  async deleteGoods(id) {
    const response = await this.request(`/goods/${id}`, {
      method: 'DELETE'
    });
    return response.ok;
  }

  // 供应商管理
  async getSuppliers() {
    const response = await this.request('/suppliers');
    return response.json();
  }

  async createSupplier(supplierData) {
    const response = await this.request('/suppliers', {
      method: 'POST',
      body: JSON.stringify(supplierData)
    });
    return response.json();
  }

  // 周转筐管理
  async getCrates() {
    const response = await this.request('/crates');
    return response.json();
  }

  async createCrate(crateData) {
    const response = await this.request('/crates', {
      method: 'POST',
      body: JSON.stringify(crateData)
    });
    return response.json();
  }

  // 仓储操作
  async getShipmentOrders() {
    const response = await this.request('/shipment-orders');
    return response.json();
  }

  async createShipmentOrder(orderData) {
    const response = await this.request('/shipment-orders', {
      method: 'POST',
      body: JSON.stringify(orderData)
    });
    return response.json();
  }

  async addScan(itemId, scanData) {
    const response = await this.request(`/shipment-order-items/${itemId}/scans`, {
      method: 'POST',
      body: JSON.stringify(scanData)
    });
    return response.json();
  }

  async completeOrder(orderId) {
    const response = await this.request(`/shipment-orders/${orderId}/complete`, {
      method: 'POST'
    });
    return response.json();
  }

  // 库存查询
  async getInventorySummary() {
    const response = await this.request('/inventory/summary');
    return response.json();
  }

  async getInventoryDetails(goodsId) {
    const response = await this.request(`/inventory/details?goods_id=${goodsId}`);
    return response.json();
  }

  async getCrateHistory(nfcUid) {
    const response = await this.request(`/history/crates?nfc_uid=${nfcUid}`);
    return response.json();
  }

  // 同步功能
  async getSyncStatus() {
    const response = await this.request('/sync/status');
    return response.json();
  }

  async sync(operations) {
    const response = await this.request('/sync', {
      method: 'POST',
      body: JSON.stringify({ operations })
    });
    return response.json();
  }
}

// 使用示例
const api = new CrateManagementAPI();

// 登录
await api.login('test2@example.com', 'test123456');

// 获取货物列表
const goods = await api.getGoods();

// 创建货物
const newGoods = await api.createGoods({
  name: "测试商品",
  description: "商品描述",
  unit: "个",
  category: "测试分类"
});

// 创建入库单据
const newOrder = await api.createShipmentOrder({
  type: "INBOUND",
  description: "入库单据",
  expectedDeliveryDate: "2024-12-31T23:59:59Z",
  items: [{
    goodsId: 1,
    supplierId: 1,
    batchNumber: "BATCH001",
    expectedQuantity: 100
  }]
});
```

## ⚠️ 错误处理

### 常见HTTP状态码

- **200**: 请求成功
- **201**: 创建成功
- **400**: 请求参数错误
- **401**: 认证失败（Token无效或过期）
- **403**: 权限不足
- **404**: 资源不存在
- **500**: 服务器内部错误

### 错误处理示例

```javascript
try {
  const response = await api.getGoods();
  console.log('成功获取货物列表:', response);
} catch (error) {
  if (error.message.includes('认证失败')) {
    // 跳转到登录页面
    window.location.href = '/login';
  } else if (error.message.includes('权限不足')) {
    alert('您没有权限执行此操作');
  } else {
    console.error('API调用失败:', error.message);
  }
}
```

## 📝 重要注意事项

1. **Token存储**: 建议使用`localStorage`或`sessionStorage`存储JWT token
2. **Token过期**: JWT token默认24小时过期，需要处理过期情况
3. **租户隔离**: 所有请求都需要携带`X-Tenant-ID`头
4. **HTTPS**: 生产环境建议使用HTTPS传输JWT token
5. **错误处理**: 实现完善的错误处理机制
6. **Loading状态**: 在API调用期间显示加载状态
7. **重试机制**: 对于网络错误实现重试机制

## 🔗 相关文档

- [API接口规范](./API_SPECIFICATION.md)
- [数据库设计](./DATABASE_SCHEMA.md)
- [功能描述](./FUNCTIONAL_DESCRIPTION.md)

---

**最后更新**: 2025-09-09  
**版本**: 1.0.0
