# Spring Boot 自定义 Starter Demo

本模块演示应用如何消费 `java-spring-boot-custom-starter`，不直接声明自动配置类，也不手动创建 `StarterGreetingService`。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-boot-custom-starter-demo -am package
```

`-am` 会同时构建 demo 依赖的 starter 模块。

## 运行

先把 starter 安装到本地 Maven 仓库：

```bash
mvn -pl java-spring-boot-custom-starter install
```

再运行 demo：

```bash
mvn -pl java-spring-boot-custom-starter-demo exec:java
```

预期输出包含：

```text
hi Custom Starter from starter demo configuration
starterGreetingService bean exists: true
```

## 使用方式

demo 模块只依赖 starter：

```xml
<dependency>
    <groupId>com.eiou</groupId>
    <artifactId>java-spring-boot-custom-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

应用代码只注入 starter 提供的 Bean：

```java
@Bean
ApplicationRunner customStarterRunner(StarterGreetingService greetingService) {
    return args -> System.out.println(greetingService.greet("Custom Starter"));
}
```

配置覆盖 starter 默认值：

```yaml
demo:
  starter:
    greeting:
      prefix: hi
      suffix: from starter demo configuration
```

## 关闭自动配置

可以通过配置关闭 starter 默认 Bean：

```yaml
demo:
  starter:
    greeting:
      enabled: false
```

也可以在应用里声明自己的 `StarterGreetingService` Bean。由于 starter 使用了 `@ConditionalOnMissingBean`，用户 Bean 会优先生效。
