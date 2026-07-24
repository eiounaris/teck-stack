# Mall Lite Order

轻量电商订单系统，用于沉淀 Java 后端求职项目。当前已完成项目骨架、用户认证和商品基础模块，后续会按订单、支付、缓存、消息队列、并发一致性逐步实现。

## 当前状态

- 已创建 Spring Boot 3.x 项目骨架
- 已接入 Web、Validation、Security、Actuator、MyBatis-Plus、Redis、RabbitMQ、Knife4j、JWT 基础依赖
- 已提供统一响应、全局异常处理、健康检查接口
- 已实现用户注册、登录、JWT Bearer Token 鉴权
- 已实现商品创建、详情、分页、上架、下架
- 已实现商品详情 Redis 缓存、空对象缓存和上下架缓存失效
- 已收紧默认访问控制，除健康检查、接口文档、注册、登录、商品浏览外，其余接口默认需要认证
- 已提供 MySQL 初始化脚本和本地 Docker Compose
- 订单、支付、消息队列等业务能力尚未实现

## 技术栈

- Java 21
- Spring Boot 3.5.16
- MyBatis-Plus 3.5.16
- MySQL 8.4
- Redis 8.8
- RabbitMQ 4.3
- Spring Security + JWT
- Knife4j / OpenAPI
- JUnit 5
- Docker Compose

## 本地依赖启动

在当前模块目录执行：

```bash
cd mall-lite-order
docker compose up -d
```

默认端口：

| 组件 | 端口 | 账号 | 密码 |
| --- | --- | --- | --- |
| MySQL | `13306` | `mall` | `mall123456` |
| Redis | `16379` | - | `mall123456` |
| RabbitMQ | `5673` | `mall` | `mall123456` |
| RabbitMQ Management | `15673` | `mall` | `mall123456` |

## 应用启动

在仓库根目录执行：

```bash
mvn -pl mall-lite-order spring-boot:run
```

健康检查：

```text
GET http://localhost:18084/api/health
```

接口文档：

```text
http://localhost:18084/doc.html
```

## 认证接口

注册：

```text
POST /api/auth/register
Content-Type: application/json

{
  "username": "demo_user",
  "password": "demo123456",
  "nickname": "Demo User"
}
```

登录：

```text
POST /api/auth/login
Content-Type: application/json

{
  "username": "demo_user",
  "password": "demo123456"
}
```

访问受保护接口：

```text
Authorization: Bearer <accessToken>
```

## 商品接口

商品分页和详情为公开浏览接口：

```text
GET /api/products?page=1&size=10&status=1&keyword=Keyboard
GET /api/products/{id}
```

商品创建、上架、下架需要登录：

```text
POST /api/products
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "name": "Mechanical Keyboard",
  "description": "Entry product for order flow testing.",
  "price": 299.00,
  "stock": 100,
  "status": 1
}
```

```text
PUT /api/products/{id}/on-sale
PUT /api/products/{id}/off-sale
```

## 后续开发顺序

1. 创建订单、扣减库存、订单明细
2. 订单取消和库存回滚
3. 模拟支付和支付幂等
4. RabbitMQ TTL + DLX 超时取消
5. 消息消费幂等
6. 并发防超卖压测
7. README、测试和简历描述完善
