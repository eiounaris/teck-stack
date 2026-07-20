# Spring 事件、异步与定时任务

本模块演示 Spring Framework 的事件发布、异步执行和定时任务，不包含 Spring MVC 和 Spring Boot。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-events-async package
```

## 运行示例

```bash
mvn -pl java-spring-events-async org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.spring.eventsasync.SpringEventsAsyncApiDemo
```

## 依赖

- `org.springframework:spring-context`：事件、异步、任务调度和容器能力

## 事件发布

```java
applicationEventPublisher.publishEvent(new OrderCompletedEvent(orderNo));
```

## 事件监听

```java
@EventListener
void onOrderCompleted(OrderCompletedEvent event) {
    // sync listener
}
```

## 异步监听

```java
@Async
@EventListener
void onOrderCompletedAsync(OrderCompletedEvent event) {
    // async listener
}
```

## 定时任务

```java
@Scheduled(fixedDelay = 500, initialDelay = 200)
void tick() {
    // scheduled task
}
```

## 代理限制

`@Async` 和 `@Transactional` 一样基于代理，同类内部调用异步方法不会生效。

## 示例文件

查看 `src/main/java/com/eiou/spring/eventsasync`。
