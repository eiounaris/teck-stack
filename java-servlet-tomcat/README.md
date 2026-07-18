# Servlet + Tomcat API 使用

本模块只演示 Servlet API、Filter、Listener 和嵌入式 Tomcat 的最小使用方式，不引入业务模型和测试。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-servlet-tomcat package
```

## 依赖

- `org.apache.tomcat.embed:tomcat-embed-core`：嵌入式 Tomcat，同时提供 Servlet API 运行环境

## 运行

```bash
mvn -pl java-servlet-tomcat exec:java
```

访问：

```text
http://localhost:8080/api?name=Servlet
```

也可以用 `curl` 查看响应头和响应体：

```bash
curl -i "http://localhost:8080/api?name=Servlet"
```

如需换端口：

```bash
mvn -Dport=18080 -pl java-servlet-tomcat exec:java
```

## Servlet API

```java
public static final class ApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        if (name == null || name.isBlank()) {
            name = "Servlet";
        }

        HttpSession session = request.getSession();
        Integer count = (Integer) session.getAttribute("count");
        count = count == null ? 1 : count + 1;
        session.setAttribute("count", count);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("X-Demo", "Servlet-Tomcat");

        response.getWriter().println("method=" + request.getMethod());
        response.getWriter().println("uri=" + request.getRequestURI());
        response.getWriter().println("name=" + name);
        response.getWriter().println("sessionCount=" + count);
        response.getWriter().println("applicationName=" + request.getServletContext().getAttribute("applicationName"));
        response.getWriter().println("requestStartNanos=" + request.getAttribute("requestStartNanos"));
    }
}
```

## Filter API

过滤器适合做请求进入 Servlet 前后的通用处理，例如日志、鉴权、统一响应头、耗时统计。

```java
public static final class RequestLoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[filter] init " + filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startNanos = System.nanoTime();
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("X-Request-Filter", RequestLoggingFilter.class.getSimpleName());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            if (request instanceof HttpServletRequest httpRequest) {
                long elapsedMillis = Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
                System.out.println("[filter] "
                        + httpRequest.getMethod()
                        + " "
                        + httpRequest.getRequestURI()
                        + " "
                        + elapsedMillis
                        + "ms");
            }
        }
    }

    @Override
    public void destroy() {
        System.out.println("[filter] destroy");
    }
}
```

在嵌入式 Tomcat 中注册过滤器：

```java
FilterDef filterDef = new FilterDef();
filterDef.setFilterName("requestLoggingFilter");
filterDef.setFilter(new RequestLoggingFilter());
context.addFilterDef(filterDef);

FilterMap filterMap = new FilterMap();
filterMap.setFilterName("requestLoggingFilter");
filterMap.addURLPatternDecoded("/*");
context.addFilterMap(filterMap);
```

## Listener API

监听器适合响应容器生命周期和请求生命周期事件，例如应用启动初始化、应用关闭清理、请求创建和销毁。

```java
public static final class DemoApplicationListener implements ServletContextListener, ServletRequestListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute("applicationName", "servlet-tomcat-demo");
        System.out.println("[listener] context initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("[listener] context destroyed");
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        event.getServletRequest().setAttribute("requestStartNanos", System.nanoTime());
        if (event.getServletRequest() instanceof HttpServletRequest request) {
            System.out.println("[listener] request initialized " + request.getRequestURI());
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        if (event.getServletRequest() instanceof HttpServletRequest request) {
            System.out.println("[listener] request destroyed " + request.getRequestURI());
        }
    }
}
```

在嵌入式 Tomcat 中注册监听器：

```java
context.addApplicationListener(DemoApplicationListener.class.getName());
```

## 请求 API

```java
request.getMethod();
request.getRequestURI();
request.getParameter("name");
request.getSession();
request.getAttribute("requestStartNanos");
request.getServletContext().getAttribute("applicationName");
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
addRequestLoggingFilter(context);
context.addApplicationListener(DemoApplicationListener.class.getName());

tomcat.start();
tomcat.getServer().await();
```

过滤器需要通过 `FilterDef` 和 `FilterMap` 挂到 `Context`；完整代码见入口类。

## 运行输出

启动后访问 `/api` 时，控制台会打印监听器和过滤器日志；HTTP 响应会包含过滤器写入的响应头和 Servlet 输出：

```text
[listener] context initialized
[filter] init requestLoggingFilter
[listener] request initialized /api
[filter] GET /api 1ms
[listener] request destroyed /api
X-Request-Filter=RequestLoggingFilter
method=GET
uri=/api
name=Servlet
sessionCount=1
applicationName=servlet-tomcat-demo
requestStartNanos=...
```

## 入口类

查看 `src/main/java/com/eiou/servlet/ServletTomcatApiDemo.java`。
