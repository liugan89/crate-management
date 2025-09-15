
---

### 文件 4: `API_SPECIFICATION.md` (API清单)

```markdown
# API Specification

This document defines the RESTful API endpoints for the backend. All endpoints are prefixed with `/api/v1`. Access control is based on JWT roles.

---
## Auth & Onboarding
### `POST /auth/register`
-   **Description**: Registers a new tenant and its primary admin user.
-   **Access**: Public
-   **Request DTO**: `RegisterRequestDTO { 
        // 公司信息
        @NotBlank String companyName; 
        @Email String contactEmail; 
        String phoneNumber; 
        String address; 
        String city; 
        String state; 
        String zipCode; 
        String country; 
        String timezone; 
        // 管理员信息
        @Email @NotBlank String email; 
        @Size(min=8) String password; 
        String fullName; 
        String phone; 
    }`
-   **Response**: `201` with `AuthResponseDTO { String token; }`

### `POST /auth/login`
-   **Description**: Authenticates a user.
-   **Access**: Public
-   **Request Body**: `LoginRequestDTO { email, password }`
-   **Response**: `200 OK` with `AuthResponseDTO { token, ... }`

---
## User & Team Management
**Access**: `@PreAuthorize("hasRole('ADMIN')")`

### `POST /users`
-   **Description**: Creates a new user within the admin's tenant.
-   **Request DTO**: `CreateUserRequestDTO { @Email @NotBlank String email; @NotBlank String initialPassword; @NotNull Role role; }`
-   **Response**: `201` with `UserDTO`

### `GET /users`
-   **Description**: Lists all users in the admin's tenant.
-   **Response**: `200 OK` with `List<UserDTO>`

### `GET /users/{userId}`
-   **Description**: Gets details of a specific user.
-   **Response**: `200 OK` with `UserDTO`

### `PUT /users/{userId}`
-   **Description**: Updates a user's information (e.g., role, active status).
-   **Request Body**: `UpdateUserRequestDTO { role, isActive }`
-   **Response**: `200 OK` with `UserDTO`

### `DELETE /users/{userId}`
-   **Description**: Deletes a user from the tenant.
-   **Response**: `204 No Content`

### `PUT /users/{userId}/reset-password`
-   **Description**: Resets a user's password.
-   **Request Body**: `ResetPasswordRequestDTO { newPassword }`
-   **Response**: `204 No Content`

---
## Subscription & Billing
### `GET /subscription`
-   **Description**: Gets the current tenant's subscription and usage details.
-   **Response**: `200 OK` with `SubscriptionDTO`

### `GET /subscription/plans`
-   **Description**: Lists all available subscription plans.
-   **Response**: `200 OK` with `List<PlanDTO>`

### `POST /subscription/change-plan`
-   **Description**: Upgrades or changes the current plan. (Payment logic TBD).
-   **Request Body**: `ChangePlanRequestDTO { planId, paymentToken }`
-   **Response**: `200 OK` with `SubscriptionDTO`

### `GET /invoices`
-   **Description**: Gets the billing history for the current tenant.
-   **Response**: `200 OK` with `List<InvoiceDTO>`

---
## Master Data
-   **Endpoints**: Standard CRUD endpoints for `/goods`, `/suppliers`, `/locations`, `/crate-types`.
    -   `GET /<resource>` - 支持查询参数：`name`（名称模糊查询）、`isActive`（激活状态过滤）
    -   `POST /<resource>`
    -   `GET /<resource>/{id}`
    -   `PUT /<resource>/{id}`
    -   `DELETE /<resource>/{id}`

### 查询参数说明
-   **GET /goods**: 
    -   `?name=关键词` - 按货物名称进行模糊查询（不区分大小写）
    -   `?isActive=true/false` - 按激活状态过滤
    -   示例：`/api/v1/goods?name=苹果&isActive=true`
-   **GET /suppliers**: 
    -   `?name=关键词` - 按供应商名称进行模糊查询（不区分大小写）
    -   `?isActive=true/false` - 按激活状态过滤
    -   示例：`/api/v1/suppliers?name=科技&isActive=true`
-   **GET /locations**: 
    -   `?name=关键词` - 按库位名称进行模糊查询（不区分大小写）
    -   `?isActive=true/false` - 按激活状态过滤
    -   示例：`/api/v1/locations?name=A区&isActive=true`

---
## Crate Management
### `GET, POST /crates`
-   **Description**: Manages crate instances (registration).

### `GET, PUT /crates/{id}`
-   **Description**: Updates a specific crate instance.

### `GET /crates/lookup`
-   **Description**: **High-frequency endpoint**. Looks up crate details by its NFC UID.
-   **Query Param**: `@RequestParam @NotBlank String nfc_uid`
-   **Response**: `200` with `CrateDetailsDTO { CrateDTO crateInfo; CrateContentDTO currentContent; }`

---
## Warehouse Operations
### `POST /shipment-orders`
-   **Description**: Creates a new shipment order (inbound, outbound, or adjustment).
-   **Request DTO**: `CreateShipmentOrderRequestDTO { @NotNull ShipmentOrderType type; String priority; String notes; LocalDateTime expectedDeliveryDate; Long originalOrderId; }`
-   **Response**: `201` with `ShipmentOrderDTO`
-   **Note**: `orderNumber` is automatically generated based on order type (e.g., IN20240115143012A1B2, OUT20240115143012C3D4)

### `GET /shipment-orders`
-   **Description**: Lists shipment orders, with filters for `type` and `status`.
-   **Response**: `200 OK` with `List<ShipmentOrderSummaryDTO>`

### `GET /shipment-orders/{id}`
-   **Description**: Gets the full details of a single shipment order, including its items and scans.
-   **Response**: `200 OK` with `ShipmentOrderDetailsDTO`

### `POST /shipment-orders/{id}/complete`
-   **Description**: **Core business logic endpoint**. Finalizes a shipment order, triggering inventory updates.
-   **Response**: `200 OK` with `ShipmentOrderDetailsDTO`

### `POST /shipment-orders/{orderId}/items`
-   **Description**: Adds an item line to a pending shipment order.
-   **Request Body**: `CreateOrderItemRequestDTO { goodsId, supplierId, ... }`
-   **Response**: `201 Created` with `OrderItemDTO`

### `POST /shipment-order-items/{itemId}/scans`
-   **Description**: **High-frequency endpoint**. Records a single NFC scan against an order item.
-   **Request DTO**: `CreateScanRequestDTO { @NotBlank String nfcUid; @Positive BigDecimal actualQuantity; @NotNull Long locationId; }`
-   **Response**: `201` with `ScanDTO { Long id; String nfcUid; ZonedDateTime scanTimestamp; BigDecimal quantity; Long locationId; String locationName; }`

### `PUT /shipment-order-items/{itemId}`
-   **Description**: Updates an order item in a pending shipment order.
-   **Request DTO**: `UpdateOrderItemRequestDTO { Long goodsId; Long supplierId; @Positive BigDecimal expectedQuantity; String batchNumber; LocalDate productionDate; }`
-   **Response**: `200` with `OrderItemDTO`
-   **Note**: Only allowed for orders in PENDING or IN_PROGRESS status

### `DELETE /shipment-order-items/{itemId}`
-   **Description**: Deletes an order item and all associated scans from a pending shipment order.
-   **Response**: `204 No Content`
-   **Note**: Only allowed for orders in PENDING or IN_PROGRESS status

### `PUT /scans/{scanId}`
-   **Description**: Updates a scan record in a pending shipment order.
-   **Request DTO**: `UpdateScanRequestDTO { @Positive BigDecimal actualQuantity; @NotNull Long locationId; }`
-   **Response**: `200` with `ScanDTO`
-   **Note**: Only allowed for orders in PENDING or IN_PROGRESS status

### `DELETE /scans/{scanId}`
-   **Description**: Deletes a scan record from a pending shipment order.
-   **Response**: `204 No Content`
-   **Note**: Only allowed for orders in PENDING or IN_PROGRESS status

---
## Inventory & History
### `GET /inventory/summary`
-   **Description**: Gets a real-time summary of inventory, grouped by goods.
-   **Response**: `200` with `List<InventorySummaryDTO { String goodsName; String sku; BigDecimal totalQuantity; }>`

### `GET /inventory/details`
-   **Description**: Gets a detailed list of all crates in stock for a specific good.
-   **Query Param**: `goods_id` (required)
-   **Response**: `200 OK` with `List<InventoryDetailDTO>`

### `GET /history/crates`
-   **Description**: Gets the full operational history for a single crate.
-   **Query Param**: `nfc_uid` (required)
-   **Response**: `200 OK` with `List<OperationLogDTO>`

---
## Offline Sync
### `POST /sync`
-   **Description**: A unified endpoint for mobile clients to submit a batch of offline operations.
-   **Request DTO**: `SyncRequestDTO { @NotEmpty List<OperationDTO> operations; }`
    - `OperationDTO { String type; JsonNode payload; String clientTimestamp; }`
-   **Response**: `200` with `SyncResponseDTO`