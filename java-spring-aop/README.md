# Spring AOP 使用

本模块演示 Spring Framework AOP 与代理机制，不包含 Spring MVC 和 Spring Boot。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-aop package
```

## 运行示例

```bash
mvn -pl java-spring-aop org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.spring.aop.SpringAopApiDemo
```

## 依赖

- `org.springframework:spring-context`：Spring 容器
- `org.springframework:spring-aop`：Spring AOP 代理支持
- `org.aspectj:aspectjweaver`：解析 `@Aspect` 切面表达式

## 开启 AOP

```java
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackageClasses = SpringAopConfig.class)
class SpringAopConfig {
}
```

## 切面

```java
@Aspect
@Component
class AuditAspect {
    @Around("execution(* com.eiou.spring.aop.BillingService.sensitiveOperation(..))")
    Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
```

## 代理限制

- Spring AOP 基于代理对象增强方法调用。
- 同一个类内部使用 `this.method()` 调用增强方法时，不会经过代理。
- `final` 类和 `final` 方法不适合被 CGLIB 代理增强。
- 事务、异步、缓存等能力也会遇到类似代理限制。

## 示例文件

查看 `src/main/java/com/eiou/spring/aop`。
