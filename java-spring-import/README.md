# Spring @Import 使用

本模块演示 Spring Framework 原生 `@Import` 机制，不引入 Spring Boot、不编写测试、不引入业务模型。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-import package
```

## 运行

```bash
mvn -pl java-spring-import exec:java
```

也可以使用完整插件坐标运行：

```bash
mvn -pl java-spring-import org.codehaus.mojo:exec-maven-plugin:3.6.3:java
```

## 依赖

- `org.springframework:spring-context`：提供 `@Configuration`、`@Bean`、`@Import`、`ImportSelector`、`ImportBeanDefinitionRegistrar` 和 `ApplicationContext`。

## 导入配置类

```java
@Configuration
@Import(PaymentConfiguration.class)
class SpringImportConfig {
}
```

`PaymentConfiguration` 中的 `@Bean` 方法会被解析，`ImportedPaymentService` 会注册到容器。

## 导入普通类

```java
@Import(AuditClient.class)
```

`@Import` 不只可以导入 `@Configuration` 类，也可以直接导入普通类。普通类会作为 imported configuration class 注册成 Bean。

## ImportSelector

`ImportSelector` 可以根据当前配置类的注解元数据动态返回要导入的类名：

```java
class FeatureImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return new String[]{CacheFeatureConfiguration.class.getName()};
    }
}
```

本模块通过 `@EnableImportDemoFeatures(mode = "cache")` 选择导入 `CacheFeatureConfiguration`。

## ImportBeanDefinitionRegistrar

`ImportBeanDefinitionRegistrar` 更底层，可以直接向 `BeanDefinitionRegistry` 注册 BeanDefinition：

```java
class FeatureBeanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("importFeatureReport", beanDefinition);
    }
}
```

它适合框架级扩展，例如 MyBatis mapper 扫描、Feign client 注册、Spring Boot 自动配置中的复杂 Bean 注册逻辑。

## 和 Spring Boot 的关系

Spring Boot 的 `@EnableAutoConfiguration` 本质上就是：

```java
@Import(AutoConfigurationImportSelector.class)
```

Boot 通过 Spring 原生 `@Import` 扩展点接入自动配置机制，再由 `AutoConfigurationImportSelector` 读取 `AutoConfiguration.imports`。

## 示例文件

查看 `src/main/java/com/eiou/spring/importdemo`。
