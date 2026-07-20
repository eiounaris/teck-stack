# Spring Boot Actuator 使用

本模块演示 Spring Boot Actuator 的基础能力，不引入业务模型、不编写测试、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-boot-actuator package
```

## 运行

```bash
mvn -pl java-spring-boot-actuator exec:java
```

如需换端口：

```bash
mvn -Dserver.port=18082 -pl java-spring-boot-actuator exec:java
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：提供嵌入式 Tomcat 和 Web 运行环境。
- `org.springframework.boot:spring-boot-starter-actuator`：提供健康检查、指标、应用信息、条件报告、日志级别等生产可观测端点。

## 基础配置

`src/main/resources/application.yml`：

```yaml
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: health,info,metrics,beans,conditions,loggers
  endpoint:
    health:
      show-details: always
```

Actuator 默认只暴露少量 Web 端点。真实项目中不要无差别暴露所有端点，尤其是 `env`、`beans`、`conditions` 这类可能包含敏感结构信息的端点。

## 常用端点

```bash
curl "http://localhost:8080/actuator"
curl "http://localhost:8080/actuator/health"
curl "http://localhost:8080/actuator/info"
curl "http://localhost:8080/actuator/metrics"
curl "http://localhost:8080/actuator/metrics/http.server.requests"
curl "http://localhost:8080/actuator/beans"
curl "http://localhost:8080/actuator/conditions"
curl "http://localhost:8080/actuator/loggers/com.eiou.spring.boot.actuator"
```

## HealthIndicator

自定义健康检查：

```java
@Component
class DemoHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up()
                .withDetail("module", "java-spring-boot-actuator")
                .build();
    }
}
```

查看：

```bash
curl "http://localhost:8080/actuator/health"
```

## InfoContributor

自定义 `/actuator/info` 内容：

```java
@Component
class DemoInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("demo", Map.of("module", "java-spring-boot-actuator"));
    }
}
```

查看：

```bash
curl "http://localhost:8080/actuator/info"
```

## Metrics

本模块提供一个轻量技术端点，用于触发自定义 Counter：

```bash
curl "http://localhost:8080/actuator-demo/ping"
curl "http://localhost:8080/actuator-demo/count"
curl "http://localhost:8080/actuator/metrics/demo.ping.count"
```

自定义指标来自 Micrometer：

```java
Counter.builder("demo.ping.count")
        .description("Counts demo ping requests")
        .register(meterRegistry);
```

## 日志级别

查看 logger：

```bash
curl "http://localhost:8080/actuator/loggers/com.eiou.spring.boot.actuator"
```

修改 logger 级别：

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  --data '{"configuredLevel":"DEBUG"}' \
  "http://localhost:8080/actuator/loggers/com.eiou.spring.boot.actuator"
```

恢复：

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  --data '{"configuredLevel":null}' \
  "http://localhost:8080/actuator/loggers/com.eiou.spring.boot.actuator"
```

## 安全提示

本模块为了演示暴露了 `beans` 和 `conditions`。真实项目中建议：

- 只暴露必要端点，例如 `health`、`info`、`metrics`。
- 对管理端点加认证和网络隔离。
- 不在公网暴露 `env`、`beans`、`conditions`、`heapdump`、`threaddump` 等敏感端点。
- 用独立管理端口隔离业务流量和管理流量。

## 示例文件

查看 `src/main/java/com/eiou/spring/boot/actuator` 和 `src/main/resources/application.yml`。
