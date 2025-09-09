# GEMINI 代码生成指令：NFC板条箱管理系统

## 1. 项目概述
本文档是为NFC板条箱管理SaaS应用的Java后端代码生成提供的 **最终、权威性指导**。请严格遵循本文档及其引用的源文档。

**首要目标**: 生成结构清晰、逻辑严谨、安全可靠且易于维护的生产级Java代码。
**交互语言**: **请在后续所有与此项目相关的代码生成和交流中，始终使用中文。**

## 2. 技术栈与核心库
- **语言/平台**: Java 17, Spring Boot 3.x
- **数据持久化**: Spring Data JPA with Hibernate
- **数据库**: PostgreSQL
- **认证/授权**: Spring Security with JWT
- **API文档**: SpringDoc (OpenAPI 3)
- **数据审计**: **Hibernate Envers**。所有需要审计的实体类，请添加`@Audited`注解。
- **构建工具**: Maven

## 3. 核心设计原则与指令
- **代码风格**: 遵循Google Java Style Guide。所有公开方法必须有Javadoc。
- **多租户实现**:
    - **数据隔离**: 所有租户隔离的实体类必须包含`@Column(name = "tenant_id", nullable = false)`字段。
    - **自动过滤**: 必须实现一个全局机制（推荐使用Hibernate `@Filter`），确保所有JPA查询都自动追加`WHERE tenant_id = :currentTenantId`条件，对业务代码透明。
- **枚举（Enum）的使用**:
    - 对于定义明确的业务状态和类型，**必须使用Java枚举**。例如：`User.Role` (`ADMIN`, `OPERATOR`), `ShipmentOrder.Type` (`INBOUND`, `OUTBOUND`, ...), `Crate.Status` (`AVAILABLE`, `IN_USE`, ...)。
    - 在JPA实体中，使用`@Enumerated(EnumType.STRING)`进行持久化，以保证数据库的可读性。
- **异常处理**:
    - 创建一个全局异常处理器`@RestControllerAdvice`。
    - 定义业务异常类，如`ResourceNotFoundException`, `BusinessValidationException`。
    - 错误响应体应为统一的JSON结构，例如：`{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "Crate with NFC UID [xxxx] not found" }`。
- **DTO设计**:
    - 严格区分请求DTO、响应DTO和JPA实体。**严禁**在Controller层直接返回JPA实体对象。
    - 优先使用Java Record定义DTO。
    - 在请求DTO中使用`jakarta.validation.constraints`注解（如`@NotNull`, `@Size`, `@Email`）进行输入验证。

## 4. 源文档 (Single Source of Truth)
- **[功能描述](./FUNCTIONAL_DESCRIPTION.md)**: 描述业务逻辑、流程和规则。**是实现Service层逻辑的唯一依据。**
- **[数据库结构](./DATABASE_SCHEMA.md)**: 定义所有表、字段、约束和关系。**是生成JPA实体类的唯一依据。**
- **[API接口规范](./API_SPECIFICATION.md)**: 定义所有RESTful端点、路径、方法和数据契约。**是生成Controller层和DTO的唯一依据。**
- **[代码生成、更新记录](./CODE_TASK.md)**: 任务跟踪文档，不同会话共同完成同一任务的上下文。**因为你可能需要多次操作才能完成，所以你需要一个稳妥的方式避免在多次操作的过程中失忆（丢失上下文）。请使用本文档来跟踪任务的执行进度，每次取得关键性的进展，先更新进度文件，然后再执行下一步。**

## 5. 代码生成工作流
1.  **枚举**: 首先根据`FUNCTIONAL_DESCRIPTION.md`中提及的各种状态，创建Java `enum`类。
2.  **实体类 (Entities)**: 严格按照`DATABASE_SCHEMA.md`生成JPA实体类。正确应用`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`, `@JoinColumn`, `@Enumerated`, `@Audited`等注解。
3.  **仓库接口 (Repositories)**: 为每个实体类生成Spring Data JPA仓库接口。
4.  **DTOs**: 严格按照`API_SPECIFICATION.md`中定义的结构，为每个端点生成请求和响应DTOs。
5.  **服务层 (Services)**:
    - 创建Service接口和实现。
    - 实现`FUNCTIONAL_DESCRIPTION.md`中描述的所有业务逻辑。
    - 所有数据库写操作的方法都必须添加`@Transactional`注解。
    - 核心业务逻辑（如完成订单、调整库存）必须是原子性的。
6.  **控制器层 (Controllers)**:
    - 严格按照`API_SPECIFICATION.md`创建`@RestController`。
    - 注入Service，调用其方法，并进行DTO与实体之间的转换。
    - 使用`@Valid`注解触发请求体验证。
7.  **安全配置 (Security)**:
    - 配置Spring Security，集成JWT认证过滤器。
    - 实现`UserDetailsService`，从数据库加载用户信息。
    - 使用`@PreAuthorize("hasRole('ADMIN')")`等注解实现基于角色的方法级访问控制。