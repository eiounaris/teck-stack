# Swagger / OpenAPI 使用

本模块演示 Spring Boot 接入 Swagger/OpenAPI 的基础用法，不引入业务模型、不编写测试、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-swagger package
```

## 运行

```bash
mvn -pl java-swagger exec:java
```

也可以使用 Spring Boot Maven 插件：

```bash
mvn -pl java-swagger spring-boot:run
```

如需换端口：

```bash
mvn -Dserver.port=18083 -pl java-swagger exec:java
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：提供 Spring MVC、嵌入式 Tomcat 和 JSON 支持。
- `org.springdoc:springdoc-openapi-starter-webmvc-ui`：生成 OpenAPI 文档并提供 Swagger UI 页面。

## 基础配置

`src/main/resources/application.yml`：

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## 访问地址

启动后访问：

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
http://localhost:8080/swagger-demo/hello?name=Swagger
```

## 最直接 API 示例

```java
@Tag(name = "swagger-demo", description = "Swagger/OpenAPI basic API")
@RestController
@RequestMapping("/swagger-demo")
public class SwaggerDemoController {
    @Operation(summary = "Return a simple greeting")
    @GetMapping("/hello")
    public Map<String, String> hello(
            @Parameter(description = "Name shown in the greeting")
            @RequestParam(name = "name", defaultValue = "Swagger") String name) {
        return Map.of("message", "hello " + name);
    }
}
```

## 示例文件

查看 `src/main/java/com/eiou/swagger` 和 `src/main/resources/application.yml`。
