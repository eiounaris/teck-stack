# Spring Test 使用

本模块演示 Spring Framework 原生测试能力，不包含 Spring MVC 和 Spring Boot。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-test test
```

## 依赖

- `org.springframework:spring-test`：Spring TestContext Framework、`SpringExtension`、事务测试支持
- `org.junit.jupiter:junit-jupiter`：JUnit 5 / JUnit Jupiter 测试框架
- `org.springframework:spring-context`：Spring 容器
- `org.springframework:spring-jdbc`：测试中的 `JdbcTemplate`
- `org.springframework:spring-tx`：测试事务管理
- `com.h2database:h2`：内存数据库驱动

## SpringExtension

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfig.class)
class PricingServiceSpringTest {
    @Autowired
    private PricingService pricingService;
}
```

## ContextConfiguration

```java
@ContextConfiguration(classes = SpringTestConfig.class)
```

`@ContextConfiguration` 用来指定测试加载的 Spring 配置类。没有 Spring Boot 时，不会自动扫描启动类，所以测试需要显式声明上下文来源。

## 注入测试对象

```java
@Autowired
private PricingService pricingService;
```

Spring Test 会先启动测试上下文，再把容器中的 Bean 注入到测试类字段、构造器或方法参数中。

## 事务测试自动回滚

```java
@Transactional
@Test
void transactionalTestRollsBackByDefault() {
    orderRepository.save("ORDER-1001");
}
```

测试方法上使用 `@Transactional` 时，Spring Test 默认会在测试结束后回滚事务，避免测试数据污染后续用例。

## DirtiesContext

```java
@DirtiesContext
```

当测试修改了 Spring 容器级别状态，且会影响其他测试时，可以使用 `@DirtiesContext` 让 Spring 丢弃当前缓存上下文。

## 示例文件

- `src/main/java/com/eiou/spring/test`：被测试的配置和业务类
- `src/test/java/com/eiou/spring/test`：Spring Test 示例
