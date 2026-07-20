# Spring Boot 配置与自动配置

本模块演示 Spring Boot 的配置与自动配置基础能力，不引入业务模型、不编写测试、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-boot package
```

## 运行

```bash
mvn -pl java-spring-boot exec:java
```

也可以使用 Spring Boot Maven 插件：

```bash
mvn -pl java-spring-boot spring-boot:run
```

访问：

```text
http://localhost:8080/boot/hello?name=SpringBoot
```

如需换端口：

```bash
mvn -Dserver.port=18080 -pl java-spring-boot exec:java
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：Web starter，自动引入 Spring MVC、Jackson、嵌入式 Tomcat、日志等常用依赖。
- `org.springframework.boot:spring-boot-maven-plugin`：Spring Boot Maven 插件，可用于 `spring-boot:run` 和可执行 Jar 打包。

## SpringBootApplication

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApiDemo.class, args);
    }
}
```

`@SpringBootApplication` 组合了：

- `@SpringBootConfiguration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

`@ConfigurationPropertiesScan` 用于扫描 `@ConfigurationProperties` 配置映射类。

## YAML 配置

`src/main/resources/application.yml`：

```yaml
spring:
  application:
    name: spring-boot-demo

server:
  port: 8080

demo:
  message: hello from application.yml
  feature:
    enabled: true
    mode: default
  client:
    base-url: https://api.example.local
    timeout: 3s
  items:
    - core
    - config
    - auto-config
```

YAML 适合表达分组配置、嵌套配置、列表和 Map；`.properties` 也支持同样能力，但嵌套配置通常没有 YAML 直观。

## Profile 配置

本模块包含：

- `application.yml`：默认配置。
- `application-dev.yml`：开发环境覆盖配置。
- `application-prod.yml`：生产环境覆盖配置。

启动 dev profile：

```bash
mvn -Dserver.port=18080 -Dspring.profiles.active=dev -pl java-spring-boot exec:java
```

启动 prod profile：

```bash
mvn -Dserver.port=18080 -Dspring.profiles.active=prod -pl java-spring-boot exec:java
```

查看当前 profile：

```bash
curl "http://localhost:8080/boot/profile"
```

## ConfigurationProperties

成组配置优先使用 `@ConfigurationProperties`，比多个 `@Value` 更适合真实项目配置绑定。

```java
@ConfigurationProperties(prefix = "demo")
class DemoProperties {
    private String message;
    private Feature feature;
    private Client client;
    private List<String> items;
    private Map<String, String> labels;
}
```

本模块演示了：

- 字符串绑定：`demo.message`
- boolean 绑定：`demo.feature.enabled`
- 嵌套对象绑定：`demo.client.base-url`
- 时间类型绑定：`demo.client.timeout=3s`
- 列表绑定：`demo.items`
- Map 绑定：`demo.labels`

查看绑定结果：

```bash
curl "http://localhost:8080/boot/properties"
```

## Value 对比

简单单值可以使用 `@Value`：

```java
@Value("${demo.message}")
private String message;
```

成组配置建议使用 `@ConfigurationProperties`，这样配置更集中、类型更明确，也更容易做 IDE 提示和配置元数据扩展。

## 条件装配

本模块演示两个常用条件：

```java
@Bean
@ConditionalOnProperty(prefix = "demo.feature", name = "enabled", havingValue = "true")
DemoFeature enabledDemoFeature(DemoProperties properties) {
    return () -> "enabled demo feature";
}

@Bean
@ConditionalOnMissingBean(DemoFeature.class)
DemoFeature defaultDemoFeature() {
    return () -> "default demo feature";
}
```

- `@ConditionalOnProperty`：根据配置决定是否注册 Bean。
- `@ConditionalOnMissingBean`：当容器里没有某类型 Bean 时提供默认实现。

查看条件装配结果：

```bash
curl "http://localhost:8080/boot/condition"
```

## 自动配置

引入 `spring-boot-starter-web` 后，Spring Boot 会根据 classpath 自动配置 Web 容器、`DispatcherServlet`、JSON 转换器、字符编码过滤器和 Spring MVC 基础组件。

```java
@GetMapping("/auto-config")
Map<String, Object> autoConfig() {
    return Map.of(
            "dispatcherServlet", applicationContext.containsBean("dispatcherServlet"),
            "jacksonObjectMapper", applicationContext.containsBean("jacksonObjectMapper")
    );
}
```

查看自动配置结果：

```bash
curl "http://localhost:8080/boot/auto-config"
```

## 外部化配置优先级

真实项目最常见的覆盖顺序可以按下面理解，越靠上优先级越高：

1. 命令行参数：`--demo.message=from-command-line`
2. JVM system properties：`-Ddemo.message=from-system-property`
3. 操作系统环境变量：`DEMO_MESSAGE=from-env`
4. Profile 配置文件：`application-dev.yml`
5. 默认配置文件：`application.yml`
6. 代码里的默认值

使用 JVM system property 覆盖：

```bash
mvn -Dserver.port=18080 -Ddemo.message=from-system-property -pl java-spring-boot exec:java
```

使用命令行参数覆盖：

```bash
mvn -pl java-spring-boot exec:java -Dexec.args="--server.port=18080 --demo.message=from-command-line"
```

查看当前观测到的配置：

```bash
curl "http://localhost:8080/boot/priority"
```

## 启动生命周期

```java
@Bean
ApplicationRunner applicationRunner(Environment environment) {
    return args -> System.out.println(environment.getProperty("spring.application.name"));
}

@Bean
CommandLineRunner commandLineRunner() {
    return args -> System.out.println(args.length);
}
```

- `ApplicationRunner`：接收封装后的 `ApplicationArguments`。
- `CommandLineRunner`：接收原始 `String[] args`。
- 两者都会在 Spring Boot 应用上下文启动完成后执行。

## 示例请求

```bash
curl "http://localhost:8080/boot/hello?name=SpringBoot"
curl "http://localhost:8080/boot/config"
curl "http://localhost:8080/boot/properties"
curl "http://localhost:8080/boot/profile"
curl "http://localhost:8080/boot/condition"
curl "http://localhost:8080/boot/priority"
curl "http://localhost:8080/boot/auto-config"
```

## 示例文件

查看 `src/main/java/com/eiou/spring/boot` 和 `src/main/resources`。
