# Spring Boot 自定义 Starter

本模块是一个最小自定义 Spring Boot starter，演示 starter 如何提供依赖、自动配置、配置属性和默认 Bean。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-boot-custom-starter package
```

## 核心原理

Spring Boot starter 不是特殊运行时机制，本质是一个普通 Jar。它通常做三件事：

- 聚合依赖：应用只引入 starter，不需要逐个声明底层依赖。
- 暴露自动配置：用 `@AutoConfiguration` 声明默认 Bean。
- 注册自动配置：在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中写入自动配置类全限定名。

Boot 启动时会读取 classpath 上的 `AutoConfiguration.imports`，再结合 `@ConditionalOnClass`、`@ConditionalOnProperty`、`@ConditionalOnMissingBean` 等条件决定是否装配。

## 自动配置注册

```text
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

内容：

```text
com.eiou.spring.boot.starter.greeting.autoconfigure.StarterGreetingAutoConfiguration
```

这是 Spring Boot 3 推荐的自动配置注册方式。

## AutoConfiguration

```java
@AutoConfiguration
@ConditionalOnClass(StarterGreetingService.class)
@EnableConfigurationProperties(StarterGreetingProperties.class)
@ConditionalOnProperty(prefix = "demo.starter.greeting", name = "enabled", havingValue = "true", matchIfMissing = true)
class StarterGreetingAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    StarterGreetingService starterGreetingService(StarterGreetingProperties properties) {
        return new StarterGreetingService(properties);
    }
}
```

- `@ConditionalOnClass`：只有目标类在 classpath 上时才启用。
- `@ConditionalOnProperty`：允许用户通过配置开关关闭 starter。
- `@ConditionalOnMissingBean`：用户自己定义同类型 Bean 时，starter 默认 Bean 自动让位。
- `@EnableConfigurationProperties`：把外部配置绑定到类型安全的属性类。

## 配置属性

```java
@ConfigurationProperties(prefix = "demo.starter.greeting")
class StarterGreetingProperties {
    private boolean enabled = true;
    private String prefix = "hello";
    private String suffix = "from custom starter";
}
```

应用侧可以用下面配置覆盖默认值：

```yaml
demo:
  starter:
    greeting:
      prefix: hi
      suffix: from application.yml
```

## 示例文件

查看 `src/main/java/com/eiou/spring/boot/starter/greeting/autoconfigure` 和 `src/main/resources/META-INF/spring`。
