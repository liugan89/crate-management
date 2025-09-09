# 功能描述 

> **开发者笔记**: 本文档是业务逻辑的“圣经”。Service层的代码实现必须严格、完整地反映本文档中描述的流程和规则。

## 模块一：租户、用户与认证
- **租户注册**: 公开接口，用于创建`Tenant`、`ADMIN`角色的`User`和14天试用`Subscription`。
- **用户登录**: 验证凭据，成功后返回包含`userId`, `tenantId`, `role`的JWT。
- **团队管理**: `ADMIN`角色用户专属功能。可以增删改查自己租户下的其他用户。创建用户时提供初始密码。

## 模块二：订阅与计费
- **套餐管理**: 通过Stripe集成实现付费订阅。`stripe_customer_id`和`stripe_subscription_id`是关键关联ID。
- **账单历史**: 查看`Invoices`表中的历史支付记录。
- **用量追踪**: 实时查询API和每日`usage_snapshots`归档表共同提供用量监控。

## 模块三：主数据管理
- **核心实体**: 为`Goods`（货物）、`Suppliers`（供应商）、`Locations`（库位）提供标准CRUD。
- **周转筐类型**: `Crate Types`作为模板，定义一类周转筐的通用物理属性。

## 模块四：仓储核心流程
- **周转筐注册**: 将物理NFC UID注册为`Crate`实例。
- **入库/出库流程**:
    1.  **创建单据**: 创建`INBOUND`或`OUTBOUND`类型的`Shipment Order`。
    2.  **添加行项**: 在单据下添加`Shipment Order Items`。
    3.  **扫码**: 在指定行项下，扫描NFC并记录实际数量到`Shipment Order Item Scans`。
    4.  **完成单据**:
        - **核心逻辑**: 这是原子性操作，必须在单个事务中完成。
        - **对每一个Scan**:
            - 找到对应的`Crate`和`Crate Contents`记录。
            - **入库**: 更新`Crate.status`为`IN_USE`，更新`CrateContents`状态为`INBOUND`，并填入货物信息[注意入库时这里是通过添加的行项信息（选择产品Category、填写批次号Batch Number、Production Date、Supplier、等需要的信息），然后在这个行项信息下扫码关联框子填写数量Weight/Quangtity、Location(每扫一个框都填写一次)，所有行项都关联完了之后提交完成入库]。
            - **出库**: 校验`CrateContents`当前是否为`INBOUND`状态，然后更新为`OUTBOUND`。
            - 记录`operation_logs`。
        - 将`Shipment Order`的状态更新为`COMPLETED`。
- **业务调整流程**:
    > **开发者笔记**: 这是确保数据严谨性的核心功能。调整单是对已完成操作的“反向记账”。
    1.  **创建调整单**: 创建`INBOUND_ADJUSTMENT`或`OUTBOUND_ADJUSTMENT`类型的`Shipment Order`，并关联原始单据ID。
    2.  **执行调整**: 在调整单中进行修正操作（如移除一个扫描记录）。
    3.  **完成调整单**:
        - **前置校验**: 在事务开始时，必须**锁定**并**重新校验**被调整周转筐的当前状态。例如，若要调整一个入库记录，必须确保该筐当前状态仍为`INBOUND`，而不是`OUTBOUND`。如果校验失败，则回滚事务并返回错误。
        - **执行反向操作**: 校验通过后，对`Crate Contents`执行与原始操作相反的更新。
        - 记录`operation_logs`，类型为`ADJUSTMENT`。

## 模块五：库存与追溯
- **实时库存**: **所有库存查询都必须直接查询`crate_contents`快照表**，禁止扫描`shipment_orders`等流水表来计算库存。
- **生命周期追溯**: 查询`operation_logs`表获取单个周转筐的完整历史记录。

## 模块六：日志与审计
- **业务日志**: `operation_logs`记录业务操作，供用户和客服追溯。
- **数据审计日志**: 由Hibernate Envers自动处理，记录核心实体的数据变更历史，供内部审计和排错。