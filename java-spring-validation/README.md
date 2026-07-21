# Spring Validation 使用

本模块演示 Spring Framework 原生 Validation 能力，不引入 Spring Boot、不编写测试、不引入业务模型。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-validation package
```

## 运行

```bash
mvn -pl java-spring-validation exec:java
```

## 依赖

- `org.springframework:spring-context`：提供 `Validator`、`DataBinder`、`LocalValidatorFactoryBean`、方法校验后处理器等基础能力。
- `org.hibernate.validator:hibernate-validator`：Jakarta Bean Validation 规范实现，支持 `@NotBlank`、`@Email`、`@Valid`、`@Min` 等注解。
- `org.codehaus.mojo:exec-maven-plugin`：运行示例入口类。

## Spring Validator

Spring 自带的 `Validator` 适合编写框架无关的自定义校验规则：

```java
@Component
class SignUpRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required");
        errors.rejectValue("username", "username.reserved", "username must not contain admin");
    }
}
```

`supports` 用来声明当前校验器支持的目标类型，`validate` 中通过 `Errors` 收集字段错误或对象级错误。

## DataBinder

`DataBinder` 可以组合多个 Spring `Validator`：

```java
DataBinder dataBinder = new DataBinder(request);
dataBinder.addValidators(beanValidator, springValidator);
dataBinder.validate();
BindingResult result = dataBinder.getBindingResult();
```

它常出现在 MVC 参数绑定背后，也可以在非 Web 场景中手动使用。

## Bean Validation

Jakarta Bean Validation 使用注解声明字段规则：

```java
class SignUpRequest {
    @NotBlank
    private String username;

    @Email
    private String email;

    @Valid
    private Address address;
}
```

`@Valid` 会触发嵌套对象校验。Spring 通过 `LocalValidatorFactoryBean` 把 Bean Validation 适配成 Spring `Validator`。

## 方法参数校验

方法参数校验需要启用 `MethodValidationPostProcessor`，目标 Bean 上使用 `@Validated`：

```java
@Service
@Validated
class RegistrationService {
    String register(@Valid SignUpRequest request, @NotBlank String operator) {
        return operator + " registered " + request.getUsername();
    }
}
```

当参数不满足约束时，会抛出 `ConstraintViolationException`。

## 配置

```java
@Configuration
@ComponentScan(basePackageClasses = SpringValidationConfig.class)
class SpringValidationConfig {
    @Bean
    LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    static MethodValidationPostProcessor methodValidationPostProcessor(jakarta.validation.Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }
}
```

本模块使用 `ParameterMessageInterpolator`，避免为了简单示例额外引入 EL 表达式依赖。

## 示例文件

查看 `src/main/java/com/eiou/spring/validation`。
