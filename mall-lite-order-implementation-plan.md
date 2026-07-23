# 轻量电商订单系统实现计划

## 1. 项目定位

- 项目名：`mall-lite-order`
- 项目类型：Spring Boot 单体后端项目
- 核心场景：用户登录后浏览商品、创建订单、扣减库存、模拟支付、超时取消、异步通知
- 求职价值：覆盖 Java 后端岗位高频能力，包括 Spring Boot、MySQL、Redis、RabbitMQ、事务、并发、缓存、接口设计、测试、部署和压测

当前阶段不建议一开始做微服务。没有实习经历和成熟项目时，单体模块化更适合展示完整业务闭环和代码质量。微服务会引入注册中心、网关、配置中心、链路追踪等额外复杂度，但面试官更关注你是否真正理解订单、库存、事务、缓存和消息一致性。

## 2. 技术栈

- 后端：Java 21、Spring Boot 3.x
- Web：Spring MVC、RESTful API
- ORM：MyBatis 或 MyBatis-Plus，建议使用 MyBatis-Plus 提升开发效率
- 数据库：MySQL 8
- 缓存：Redis、Redisson
- 消息队列：RabbitMQ
- 安全认证：Spring Security + JWT
- 接口文档：Knife4j / Swagger OpenAPI
- 测试：JUnit 5、Mockito、Spring Boot Test
- 部署：Docker Compose
- 压测：JMeter、wrk 或 ApacheBench
- 构建：Maven

## 3. 推荐目录结构

建议在当前仓库新增一个独立模块：

```text
mall-lite-order
├── src/main/java/com/eiou/mall
│   ├── MallLiteOrderApplication.java
│   ├── common
│   │   ├── api
│   │   ├── config
│   │   ├── exception
│   │   ├── security
│   │   └── util
│   ├── user
│   ├── product
│   ├── inventory
│   ├── order
│   ├── payment
│   └── mq
├── src/main/resources
│   ├── application.yml
│   ├── mapper
│   └── db
│       ├── schema.sql
│       └── data.sql
├── src/test/java/com/eiou/mall
├── Dockerfile
├── docker-compose.yml
└── README.md
```

建议按业务包组织，而不是按 `controller/service/mapper` 横向拆分。这样面试时更容易讲清楚模块边界。

## 4. 核心模块

- 用户模块：注册、登录、JWT 鉴权、用户信息查询
- 商品模块：商品列表、商品详情、上下架状态、分页查询
- 库存模块：库存查询、库存扣减、库存回滚、防止超卖
- 订单模块：创建订单、查询订单、取消订单、订单状态流转
- 支付模块：模拟支付、支付成功回调、订单状态更新
- MQ 模块：订单创建事件、订单超时取消、支付成功通知
- 通用模块：统一响应、统一异常、参数校验、日志、全局错误码

## 5. 数据库设计

最小可用表：

```text
user
- id
- username
- password
- nickname
- status
- created_at
- updated_at

product
- id
- name
- description
- price
- status
- stock
- created_at
- updated_at

order
- id
- order_no
- user_id
- total_amount
- status
- expire_time
- paid_at
- created_at
- updated_at

order_item
- id
- order_id
- product_id
- product_name
- price
- quantity
- created_at

inventory_log
- id
- product_id
- order_no
- quantity
- operation_type
- created_at

payment
- id
- order_no
- pay_no
- amount
- status
- paid_at
- created_at

mq_message_log
- id
- message_id
- business_key
- message_type
- status
- retry_count
- created_at
- updated_at
```

简历项目不需要几十张表。表少但链路完整，比表多但业务空更有价值。

## 6. 订单状态机

建议定义明确状态：

```text
CREATED      已创建，待支付
PAID         已支付
CANCELLED    已取消
CLOSED       已关闭
```

核心状态流转：

```text
创建订单 -> CREATED
CREATED -> PAID
CREATED -> CANCELLED
CREATED -> CLOSED
```

约束：

- 已支付订单不能取消
- 已取消订单不能支付
- 重复支付请求必须幂等
- 超时取消只能取消 `CREATED` 状态订单

## 7. 核心接口

用户接口：

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/users/me
```

商品接口：

```text
GET /api/products
GET /api/products/{id}
POST /api/products
PUT /api/products/{id}
PUT /api/products/{id}/on-sale
PUT /api/products/{id}/off-sale
```

订单接口：

```text
POST /api/orders
GET  /api/orders/{orderNo}
GET  /api/orders/my
POST /api/orders/{orderNo}/cancel
```

支付接口：

```text
POST /api/payments/mock-pay
POST /api/payments/callback
GET  /api/payments/{orderNo}
```

管理接口可先简单实现，不要做复杂后台系统。

## 8. 关键技术点设计

### 8.1 登录认证

- 用户登录成功后签发 JWT
- 请求进入系统时通过 Spring Security Filter 校验 Token
- 用户 ID 放入 SecurityContext
- 业务接口从当前登录用户中获取 `userId`

### 8.2 创建订单

核心流程：

```text
校验用户登录
校验商品存在且上架
校验库存充足
扣减库存
创建订单
创建订单明细
发送订单创建消息
返回订单号
```

事务边界：

- 扣库存、创建订单、创建订单明细必须在同一个数据库事务内
- MQ 消息发送不要直接假设一定成功，先做简单版本，后续再加消息表增强可靠性

### 8.3 防止超卖

建议分两版实现。

第一版：MySQL 乐观扣减。

```sql
UPDATE product
SET stock = stock - #{quantity}
WHERE id = #{productId}
  AND stock >= #{quantity}
```

如果影响行数为 0，说明库存不足。

第二版：Redisson 分布式锁。

```text
lock:product:{productId}
```

面试时可以比较两种方案：

- MySQL 条件更新简单可靠，适合中低并发
- Redis 分布式锁可保护复杂业务临界区，但要考虑锁超时、续期、释放安全

### 8.4 商品缓存

商品详情缓存：

```text
key: mall:product:detail:{productId}
ttl: 10-30 分钟，加随机过期时间
```

缓存策略：

- 查询商品：先查 Redis，未命中查 MySQL，再写 Redis
- 修改商品：更新数据库后删除缓存
- 防缓存穿透：空对象缓存或布隆过滤器，项目中先实现空对象缓存
- 防缓存击穿：热点商品用互斥锁重建缓存

### 8.5 订单超时取消

用 RabbitMQ TTL + 死信队列实现：

```text
order.created.queue
order.delay.queue
order.dead.queue
```

流程：

- 创建订单后发送延迟消息
- 消息过期后进入死信队列
- 消费死信消息，检查订单是否仍为 `CREATED`
- 如果仍未支付，取消订单并回滚库存

如果本地 RabbitMQ 没有延迟插件，TTL + DLX 更稳。

### 8.6 支付幂等

模拟支付接口要处理重复请求：

```text
根据 orderNo 查询订单
如果订单已 PAID，直接返回成功
如果订单已 CANCELLED/CLOSED，拒绝支付
如果订单 CREATED，更新为 PAID
插入 payment 记录，pay_no 加唯一索引
```

支付回调也要幂等，避免重复更新订单。

### 8.7 消息消费幂等

使用 `mq_message_log`：

```text
消费前根据 message_id 查询是否已处理
未处理则执行业务
成功后记录 PROCESSED
失败记录 FAILED 或重试次数
```

简化版也可以先用业务唯一键约束，例如 `order_no + message_type`。

## 9. 实现阶段

### 第 1 阶段：项目骨架，1-2 天

- 新建 `mall-lite-order` Maven 模块
- 接入 Spring Boot、MySQL、MyBatis、Lombok、Validation
- 配置统一响应 `ApiResponse`
- 配置全局异常处理 `GlobalExceptionHandler`
- 配置 Knife4j
- 编写 README 初版

验收标准：

- 项目可以启动
- `/doc.html` 或 Swagger 页面可访问
- 健康检查接口可用

### 第 2 阶段：用户与认证，2-3 天

- 实现用户注册、登录
- 密码 BCrypt 加密
- JWT 生成与校验
- Spring Security 拦截器
- 当前用户上下文工具类

验收标准：

- 未登录不能访问订单接口
- 登录后携带 Token 可以访问受保护接口
- Token 失效返回统一错误码

### 第 3 阶段：商品模块，2-3 天

- 商品 CRUD
- 商品分页
- 商品上下架
- 商品详情缓存
- 修改商品后删除缓存

验收标准：

- 商品列表可分页查询
- 商品详情命中 Redis 缓存
- 商品更新后缓存不会读到旧数据

### 第 4 阶段：订单基础链路，3-5 天

- 创建订单
- 扣减库存
- 创建订单明细
- 查询我的订单
- 手动取消订单
- 库存回滚

验收标准：

- 下单成功后库存减少
- 库存不足时下单失败
- 取消未支付订单后库存恢复
- 已支付订单不能取消

### 第 5 阶段：支付与状态机，2-3 天

- 模拟支付接口
- 支付记录表
- 支付成功更新订单
- 重复支付幂等处理

验收标准：

- `CREATED` 订单可以支付
- `PAID` 订单重复支付返回成功但不重复扣款
- `CANCELLED` 订单不能支付

### 第 6 阶段：RabbitMQ 异步与超时取消，4-5 天

- 接入 RabbitMQ
- 创建订单后发送订单事件
- 实现 TTL + 死信队列
- 超时未支付自动取消
- 消费幂等记录

验收标准：

- 创建订单后不支付，超过设定时间自动取消
- 已支付订单不会被超时任务取消
- 重复消费消息不会导致库存重复回滚

### 第 7 阶段：并发与一致性强化，4-6 天

- 使用 MySQL 条件更新防止超卖
- 可选加入 Redisson 分布式锁版本
- 增加库存扣减日志
- 增加关键事务测试
- 模拟高并发下单

验收标准：

- 并发请求下库存不会小于 0
- 成功订单数不超过初始库存
- 订单数、库存变化、库存日志能对账

### 第 8 阶段：测试、压测、文档，4-7 天

- 单元测试：Service 层核心逻辑
- 集成测试：订单创建、支付、取消链路
- 接口文档：补充请求参数和响应示例
- README：启动方式、架构图、核心流程图、技术亮点
- 压测：商品详情接口、下单接口
- SQL 优化：Explain 分析订单查询、商品分页查询

验收标准：

- `mvn test` 可通过
- Docker Compose 可启动 MySQL、Redis、RabbitMQ
- README 能让别人 10 分钟内跑起来
- 有压测前后对比数据

## 10. 推荐开发顺序

严格按这个顺序做：

```text
项目骨架
用户认证
商品管理
订单创建
库存扣减
支付模拟
订单取消
Redis 缓存
RabbitMQ 超时取消
并发防超卖
测试与压测
README 与简历整理
```

不要一开始就写 MQ、缓存、分布式锁。先让主链路跑通，再逐步增强。否则容易做成一堆半成品。

## 11. README 应包含的内容

README 不要只写“如何启动”，要写成项目说明书：

```text
1. 项目背景
2. 技术栈
3. 系统架构图
4. 数据库 ER 图
5. 订单状态机
6. 创建订单流程
7. 超时取消流程
8. 防超卖方案
9. 缓存设计
10. MQ 幂等设计
11. 本地启动方式
12. 接口文档地址
13. 压测结果
14. 后续优化方向
```

## 12. 简历最终呈现

项目经历可以写成：

```text
轻量电商订单系统 | Spring Boot / MyBatis / MySQL / Redis / RabbitMQ

- 设计并实现用户认证、商品查询、订单创建、库存扣减、模拟支付、超时取消等核心链路，完成从下单到支付的订单状态流转。
- 使用 MySQL 条件更新和事务控制实现库存扣减，解决并发下单场景中的超卖问题，并通过压测验证库存一致性。
- 使用 Redis 缓存商品详情，结合随机 TTL、空对象缓存和缓存删除策略，降低高频商品查询对数据库的访问压力。
- 基于 RabbitMQ TTL + 死信队列实现订单超时自动取消，并通过消息消费幂等机制避免重复消费导致的库存重复回滚。
```

后续应把真实压测数据补进去，竞争力会明显提升。

## 13. 最终验收标准

这个项目做到可以投简历，至少满足：

- 能本地一键启动
- 有完整 README
- 有接口文档
- 有核心业务闭环
- 有数据库初始化脚本
- 有 Redis 缓存场景
- 有 RabbitMQ 异步场景
- 有并发库存防超卖
- 有核心测试
- 有压测数据
- 能画出订单流程、缓存流程、MQ 流程、事务边界

## 14. 下一步建议

下一步直接开始建 `mall-lite-order` 模块，优先完成项目骨架、POM、目录结构、基础配置、数据库脚本和第一批健康检查接口。
