# Spring Core 使用

本模块演示 Spring Framework 的 IoC / DI 核心能力，不包含 Spring MVC 和 Spring Boot。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-core package
```

## 运行示例

```bash
mvn -pl java-spring-core org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.spring.core.SpringCoreApiDemo
```

## 依赖

- `org.springframework:spring-context`：Spring 容器、注解配置、组件扫描、事件和基础扩展能力

## ApplicationContext

```java
try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext(SpringCoreConfig.class)) {
    OrderService orderService = context.getBean(OrderService.class);
    orderService.placeOrder("ORDER-1001");
}
```

## Java Config

```java
@Configuration
@ComponentScan(basePackageClasses = SpringCoreConfig.class)
class SpringCoreConfig {
    @Bean
    @Primary
    PaymentGateway alipayPaymentGateway() {
        return new NamedPaymentGateway("alipay");
    }
}
```

## 构造器注入

```java
@Service
class OrderService {
    OrderService(OrderRepository orderRepository,
                 @Qualifier("wechatPaymentGateway") PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }
}
```

构造器注入适合真实项目，因为依赖不可变、测试友好，也能更早暴露缺失依赖。

## Bean 选择

- `@Primary`：当同一类型有多个 Bean 时提供默认候选。
- `@Qualifier`：按名称或限定符选择具体 Bean。
- `@Scope("prototype")`：每次从容器获取都创建新实例。

## 示例文件

查看 `src/main/java/com/eiou/spring/core`。
