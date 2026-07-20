# Spring MVC 使用

本模块演示 Spring MVC 的最小运行方式和常用 Web API，不引入 Spring Boot、不编写测试、不引入业务模型。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-mvc package
```

## 运行

```bash
mvn -pl java-spring-mvc exec:java
```

访问：

```text
http://localhost:8080/mvc/hello?name=SpringMVC
```

如需换端口：

```bash
mvn -Dport=18080 -pl java-spring-mvc exec:java
```

## 依赖

- `org.springframework:spring-webmvc`：Spring MVC、`DispatcherServlet`、控制器映射、参数绑定、响应处理
- `org.apache.tomcat.embed:tomcat-embed-core`：嵌入式 Tomcat 运行环境

## DispatcherServlet

```java
AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
webApplicationContext.register(SpringMvcConfig.class);

DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
context.addServletMappingDecoded("/", "dispatcherServlet");
```

`DispatcherServlet` 是 Spring MVC 的前端控制器，请求进入后由它完成 HandlerMapping、HandlerAdapter、参数绑定、异常处理和响应写出。

## MVC 配置

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = SpringMvcConfig.class)
class SpringMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestTraceInterceptor()).addPathPatterns("/mvc/**");
    }
}
```

## Controller

```java
@RestController
@RequestMapping("/mvc")
class BasicController {
    @GetMapping("/hello")
    String hello(@RequestParam(defaultValue = "Spring MVC") String name) {
        return "hello " + name;
    }
}
```

## 常用注解

- `@RestController`：等价于 `@Controller` + `@ResponseBody`。
- `@RequestMapping`：声明控制器或方法的请求路径。
- `@GetMapping` / `@PostMapping`：声明 HTTP 方法和路径。
- `@RequestParam`：绑定查询参数或表单参数。
- `@PathVariable`：绑定 URI 路径变量。
- `@RequestHeader`：绑定请求头。
- `@RequestBody`：读取请求体。
- `@ExceptionHandler`：处理控制器异常。
- `@RestControllerAdvice`：声明全局 REST 异常处理器。

## ExceptionHandler

控制器内部的 `@ExceptionHandler` 只处理当前控制器中的匹配异常，适合处理局部规则：

```java
@ExceptionHandler(IllegalArgumentException.class)
ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("bad request: " + exception.getMessage());
}
```

## RestControllerAdvice

`@RestControllerAdvice` 适合放全局异常处理逻辑。它等价于 `@ControllerAdvice` + `@ResponseBody`，返回值会直接写入 HTTP 响应体。

```java
@RestControllerAdvice
class GlobalRestControllerAdvice {
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("global advice: " + exception.getMessage());
    }
}
```

当局部 `@ExceptionHandler` 和全局 `@RestControllerAdvice` 都能处理同一类异常时，当前控制器内的局部处理器通常更贴近该控制器语义；全局 Advice 更适合兜底和跨控制器统一处理。

## 示例请求

```bash
curl "http://localhost:8080/mvc/hello?name=SpringMVC"
curl "http://localhost:8080/mvc/path/framework"
curl -H "X-Demo: SpringMVC" "http://localhost:8080/mvc/header"
curl -i "http://localhost:8080/mvc/error"
curl -i "http://localhost:8080/mvc/advice-error"
curl -X POST -H "Content-Type: text/plain" --data "raw body" "http://localhost:8080/mvc/body"
```

## 示例文件

查看 `src/main/java/com/eiou/spring/mvc`。
