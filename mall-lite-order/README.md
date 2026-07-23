# Mall Lite Order

轻量电商订单系统，用于沉淀 Java 后端求职项目。当前模块处于项目骨架阶段，后续会按用户、商品、订单、支付、缓存、消息队列、并发一致性逐步实现。

## 当前状态

- 已创建 Spring Boot 3.x 项目骨架
- 已接入 Web、Validation、Security、Actuator、MyBatis-Plus、Redis、RabbitMQ、Knife4j、JWT 基础依赖
- 已提供统一响应、全局异常处理、健康检查接口
- 已提供 MySQL 初始化脚本和本地 Docker Compose
- 业务模块尚未实现

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

## 后续开发顺序

1. 用户注册、登录、JWT 鉴权
2. 商品 CRUD、分页、上下架
3. 商品详情缓存
4. 创建订单、扣减库存、订单明细
5. 订单取消和库存回滚
6. 模拟支付和支付幂等
7. RabbitMQ TTL + DLX 超时取消
8. 消息消费幂等
9. 并发防超卖压测
10. README、测试和简历描述完善
