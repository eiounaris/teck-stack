# SLF4J + Logback API 使用

本模块只演示 SLF4J + Logback 的构建依赖、常用 API 调用和基础功能。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-slf4j-logback package
```

## 依赖

- `org.slf4j:slf4j-api`：代码中调用的日志 API
- `ch.qos.logback:logback-classic`：SLF4J 的 Logback 实现

## 获取 Logger

```java
private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLogbackApiDemo.class);
```

## 常用 API

```java
LOGGER.trace("trace message");
LOGGER.debug("debug message");
LOGGER.info("info message");
LOGGER.warn("warn message");
LOGGER.error("error message");
```

## 占位符

```java
LOGGER.info("hello, {}", "SLF4J");
LOGGER.info("name={}, count={}", "demo", 3);
```

## 异常日志

```java
try {
    throw new IllegalStateException("demo exception");
} catch (IllegalStateException exception) {
    LOGGER.error("exception message", exception);
}
```

## MDC

```java
MDC.put("traceId", "TRACE-001");
LOGGER.info("message with traceId");
MDC.remove("traceId");
```

## Fluent API

```java
LOGGER.atInfo()
        .addKeyValue("style", "fluent")
        .log("message from fluent api");
```

## 配置文件

Logback 配置位于：

```text
src/main/resources/logback.xml
```

当前配置演示：

- ConsoleAppender 控制台输出
- 日志格式 pattern
- 包级别日志等级 `com.eiou.logging=DEBUG`
- MDC 中的 `traceId`

## 入口类

运行 `com.eiou.logging.Slf4jLogbackApiDemo` 即可查看输出。
