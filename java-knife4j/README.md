# Knife4j 使用

本模块演示 Spring Boot 接入 Knife4j 的基础用法，不引入业务模型、不编写测试、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-knife4j package
```

## 运行

```bash
mvn -pl java-knife4j exec:java
```

也可以使用 Spring Boot Maven 插件：

```bash
mvn -pl java-knife4j spring-boot:run
```

如需换端口：

```bash
mvn -Dserver.port=18084 -pl java-knife4j exec:java
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：提供 Spring MVC、嵌入式 Tomcat 和 JSON 支持。
- `com.baizhukui:knife4j-openapi3-jakarta-spring-boot-starter`：提供 Knife4j UI 和 OpenAPI 3 文档能力。

## 基础配置

`src/main/resources/application.yml`：

```yaml
knife4j:
  enable: true
```

## 访问地址

启动后访问：

```text
http://localhost:8080/doc.html
http://localhost:8080/v3/api-docs
http://localhost:8080/knife4j-demo/hello?name=Knife4j
```

## 最直接 API 示例

```java
@Tag(name = "knife4j-demo", description = "Knife4j basic API")
@RestController
@RequestMapping("/knife4j-demo")
public class Knife4jDemoController {
    @Operation(summary = "Return a simple greeting")
    @GetMapping("/hello")
    public Map<String, String> hello(
            @Parameter(description = "Name shown in the greeting")
            @RequestParam(name = "name", defaultValue = "Knife4j") String name) {
        return Map.of("message", "hello " + name);
    }
}
```

## 示例文件

查看 `src/main/java/com/eiou/knife4j` 和 `src/main/resources/application.yml`。
