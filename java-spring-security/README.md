# Spring Security 基础使用

本模块演示 Spring Security 的最小 Web 安全配置，不引入业务模型、不编写测试、不做复杂封装。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-security package
```

## 运行

```bash
mvn -pl java-spring-security exec:java
```

如需换端口：

```bash
mvn -Dserver.port=18084 -pl java-spring-security exec:java
```

## 依赖

- `org.springframework.boot:spring-boot-starter-web`：提供嵌入式 Tomcat 和 Web 运行环境。
- `org.springframework.boot:spring-boot-starter-security`：提供 Spring Security Web 过滤器链、认证、授权和默认登录能力。

## 基础配置

`SecurityFilterChain` 是 Spring Security 6 推荐的显式配置入口：

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/security/public").permitAll()
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .build();
}
```

本模块的规则：

- `/security/public`：允许匿名访问。
- 其他请求：需要认证。
- 开启 HTTP Basic：方便用 `curl -u user:password` 验证。
- 开启表单登录：浏览器访问受保护地址时跳转到默认登录页。

## 内存用户

```java
@Bean
UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();
    return new InMemoryUserDetailsManager(user);
}
```

`{noop}` 仅用于演示明文密码存储。真实项目应使用 `BCryptPasswordEncoder` 等密码编码器，并从数据库、LDAP、OAuth2/OIDC 等身份源加载用户。

## API 示例

公开接口：

```bash
curl "http://localhost:8080/security/public"
```

受保护接口，未认证会返回 `401`：

```bash
curl -i "http://localhost:8080/security/private"
```

受保护接口，使用 HTTP Basic 认证：

```bash
curl -u user:password "http://localhost:8080/security/private"
```

浏览器访问下面地址会看到默认登录页：

```text
http://localhost:8080/security/private
```

## 示例文件

查看 `src/main/java/com/eiou/spring/security` 和 `src/main/resources/application.yml`。
