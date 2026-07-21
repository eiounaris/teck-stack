# Spring Boot Test 使用

本模块演示 Spring Boot 测试常用能力。模块本身用于演示测试框架，所以包含测试代码；示例对象只保留最小 greeting、配置映射和 JSON record，不引入业务模型、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-boot-test test
```

运行单个测试类：

```bash
mvn -pl java-spring-boot-test -Dtest=GreetingControllerWebMvcSliceTest test
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：提供 MVC、JSON 和嵌入式 Web 环境，便于演示 Web 测试。
- `org.springframework.boot:spring-boot-starter-test`：聚合 Spring Boot Test、Spring Test、JUnit Jupiter、AssertJ、Mockito、JsonPath 等测试常用依赖。
- `maven-surefire-plugin`：为 Mockito 配置 `-javaagent`，避免新版本 JDK 下 inline mock maker 动态 attach 的提示和兼容性问题。

## SpringBootTest

`@SpringBootTest` 会加载完整 Spring Boot 应用上下文，适合验证配置装配、自动配置和跨层协作。

```java
@SpringBootTest(properties = "demo.greeting.message=hello from @SpringBootTest")
class SpringBootContextTest {
}
```

本模块用它演示：

- 完整上下文加载：`SpringBootContextTest`
- 内联测试属性覆盖：`@SpringBootTest(properties = "...")`
- 随机端口 HTTP 测试：`SpringBootTest.WebEnvironment.RANDOM_PORT`

## MockMvc

完整上下文中使用 MockMvc：

```java
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerMockMvcTest {
}
```

这种方式不会启动真实 Web Server，但会经过 Spring MVC 调度链，适合验证 Controller、过滤器、参数绑定和 JSON 响应。

## Random Port

启动真实嵌入式 Web Server：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerRandomPortTest {
    @Autowired
    private TestRestTemplate restTemplate;
}
```

`TestRestTemplate` 会自动指向随机端口，适合更接近真实 HTTP 调用的集成测试。

本仓库的随机端口示例使用 `ServerSocketAvailableCondition`。如果当前运行环境不允许打开监听 socket，该测试会自动跳过；在普通本地开发机或 CI 容器中通常会正常执行。

## WebMvcTest

`@WebMvcTest` 是 MVC slice test，只加载 MVC 相关组件和指定 Controller：

```java
@WebMvcTest(GreetingController.class)
class GreetingControllerWebMvcSliceTest {
    @MockitoBean
    private GreetingService greetingService;
}
```

Controller 依赖的协作者用 `@MockitoBean` 替换。这样测试启动更快，也更聚焦 Web 层行为。

## JsonTest

`@JsonTest` 只加载 JSON 序列化相关配置：

```java
@JsonTest
class GreetingJsonTest {
    @Autowired
    private JacksonTester<GreetingResponse> json;
}
```

适合单独验证 Jackson record、字段名、日期格式、自定义序列化器等 JSON 行为。

## Profile 与测试配置

测试 profile 使用 `@ActiveProfiles`：

```java
@SpringBootTest
@ActiveProfiles("test")
class SpringBootProfileTest {
}
```

本模块提供 `src/test/resources/application-test.yml`，测试 profile 激活后会覆盖默认配置。

## DynamicPropertySource

动态属性适合把测试运行时生成的值注入 Spring Environment，例如随机端口、临时目录、容器地址：

```java
@DynamicPropertySource
static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("demo.greeting.message", () -> "hello from dynamic property");
}
```

本模块没有引入 Testcontainers，但真实项目里常把 `@DynamicPropertySource` 和 Testcontainers 搭配使用。

## 常见选择

- 验证完整自动配置：使用 `@SpringBootTest`。
- 验证 Controller 请求响应：优先考虑 `@WebMvcTest`，需要完整链路时再使用 `@SpringBootTest + @AutoConfigureMockMvc`。
- 验证真实 HTTP 端口：使用 `@SpringBootTest(webEnvironment = RANDOM_PORT)`。
- 验证 JSON 序列化：使用 `@JsonTest`。
- 覆盖测试配置：使用 `@SpringBootTest(properties)`、`@ActiveProfiles` 或 `@DynamicPropertySource`。

## 示例文件

- `src/main/java/com/eiou/spring/boot/test`：最小 Boot 应用、Controller、Service、配置映射和响应 record。
- `src/test/java/com/eiou/spring/boot/test`：Spring Boot Test 示例。
- `src/test/resources/application-test.yml`：测试 profile 配置。
