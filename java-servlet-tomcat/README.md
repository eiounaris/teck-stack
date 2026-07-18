# Servlet + Tomcat API 使用

本模块只演示 Servlet API 和嵌入式 Tomcat 的最小使用方式，不引入业务模型和测试。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-servlet-tomcat package
```

## 依赖

- `org.apache.tomcat.embed:tomcat-embed-core`：嵌入式 Tomcat，同时提供 Servlet API 运行环境

## 运行一次

启动 Tomcat、请求一次 `/api`，然后自动停止：

```bash
mvn -pl java-servlet-tomcat exec:java -Dexec.args=--once
```

## 长驻运行

```bash
mvn -pl java-servlet-tomcat exec:java
```

访问：

```text
http://localhost:8080/api?name=Servlet
```

如需换端口：

```bash
mvn -Dport=18080 -pl java-servlet-tomcat exec:java -Dexec.args=--once
```

## Servlet API

```java
public static final class ApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("X-Demo", "Servlet-Tomcat");
        response.getWriter().println("Hello " + name);
    }
}
```

## 请求 API

```java
request.getMethod();
request.getRequestURI();
request.getParameter("name");
request.getSession();
```

## 响应 API

```java
response.setStatus(HttpServletResponse.SC_OK);
response.setContentType("text/plain;charset=UTF-8");
response.setHeader("X-Demo", "Servlet-Tomcat");
response.getWriter().println("response body");
```

## Tomcat API

```java
Tomcat tomcat = new Tomcat();
tomcat.setPort(8080);
tomcat.getConnector();

Context context = tomcat.addContext("", baseDirectory);
Tomcat.addServlet(context, "apiServlet", new ApiServlet());
context.addServletMappingDecoded("/api", "apiServlet");

tomcat.start();
tomcat.getServer().await();
```

## 入口类

查看 `src/main/java/com/eiou/servlet/ServletTomcatApiDemo.java`。
