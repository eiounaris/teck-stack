# Spring Bean 生命周期

本模块演示 Spring Framework Bean 生命周期和容器扩展点，不包含 Spring MVC 和 Spring Boot。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-lifecycle package
```

## 运行示例

```bash
mvn -pl java-spring-lifecycle org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.spring.lifecycle.SpringLifecycleApiDemo
```

## 依赖

- `org.springframework:spring-context`：Spring 容器与生命周期扩展点
- `jakarta.annotation:jakarta.annotation-api`：`@PostConstruct` 和 `@PreDestroy`

## 常见生命周期钩子

```java
@PostConstruct
void postConstruct() {
    // dependency injection completed
}

@PreDestroy
void preDestroy() {
    // context is closing
}
```

## InitializingBean / DisposableBean

```java
class InventoryClient implements InitializingBean, DisposableBean {
    @Override
    public void afterPropertiesSet() {
        // after properties set
    }

    @Override
    public void destroy() {
        // before bean destruction
    }
}
```

## BeanPostProcessor

```java
@Bean
static BeanPostProcessor tracingBeanPostProcessor() {
    return new BeanPostProcessor() {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }
    };
}
```

## BeanFactoryPostProcessor

`BeanFactoryPostProcessor` 在普通 Bean 实例化之前运行，适合修改 BeanDefinition 或读取容器元数据。

## 示例文件

查看 `src/main/java/com/eiou/spring/lifecycle`。
