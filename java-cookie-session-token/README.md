# Cookie + Session + Token 使用

本模块用嵌入式 Tomcat 演示三种常见 Web 状态和认证方式：

- Cookie：数据保存在客户端，每次请求由浏览器带回服务端
- Session：浏览器只保存 `JSESSIONID`，用户状态保存在服务端内存中
- Token：服务端签发带过期时间的 Bearer Token，后续请求用 `Authorization` 头携带

## 构建

在仓库根目录执行：

```bash
mvn -pl java-cookie-session-token package
```

## 运行

```bash
mvn -pl java-cookie-session-token exec:java
```

默认端口为 `8081`。如需换端口：

```bash
mvn -Dport=18081 -pl java-cookie-session-token exec:java
```

启动后访问：

```text
http://localhost:8081/
```

## Cookie 演示

第一次请求会写入 `demo_client_id` 和 `demo_cookie_visits`，后续请求会读回并递增访问次数。

```bash
curl -i -c /tmp/cst-cookies.txt -b /tmp/cst-cookies.txt "http://localhost:8081/cookie"
curl -i -c /tmp/cst-cookies.txt -b /tmp/cst-cookies.txt "http://localhost:8081/cookie"
```

关键 API：

```java
request.getCookies();
response.addCookie(new Cookie("demo_client_id", clientId));
```

## Session 演示

登录接口会创建服务端 Session，并由 Tomcat 通过 `Set-Cookie: JSESSIONID=...` 告诉客户端保存会话 ID。

```bash
curl -i -c /tmp/cst-session.txt "http://localhost:8081/session/login?user=Alice"
curl -i -b /tmp/cst-session.txt "http://localhost:8081/session/profile"
curl -i -b /tmp/cst-session.txt "http://localhost:8081/session/logout"
curl -i -b /tmp/cst-session.txt "http://localhost:8081/session/profile"
```

关键 API：

```java
HttpSession session = request.getSession(true);
session.setAttribute("sessionUser", user);
request.getSession(false);
session.invalidate();
```

## Token 演示

登录接口会返回一个演示用 Bearer Token。Token 由 `subject.expiration.signature` 组成，签名使用 HMAC-SHA256。

```bash
TOKEN=$(curl -s "http://localhost:8081/token/login?user=Alice" | awk -F= '/^token=/{print $2}')
curl -i -H "Authorization: Bearer $TOKEN" "http://localhost:8081/token/profile"
```

关键 API：

```java
String authorization = request.getHeader("Authorization");
Mac mac = Mac.getInstance("HmacSHA256");
```

## 对比

| 方式 | 客户端保存 | 服务端保存 | 常见用途 |
| --- | --- | --- | --- |
| Cookie | 具体键值 | 可选 | 轻量偏好、追踪标识、会话 ID |
| Session | `JSESSIONID` | 用户状态 | 传统服务端登录态 |
| Token | 已签名凭证 | 通常无状态 | API、前后端分离、移动端认证 |

## 入口类

查看 `src/main/java/com/eiou/auth/CookieSessionTokenApiDemo.java`。
