package com.eiou.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class ServletTomcatApiDemo {
    private static final int DEFAULT_PORT = 8080;

    private ServletTomcatApiDemo() {
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("port", DEFAULT_PORT);
        Tomcat tomcat = startTomcat(port);
        String url = "http://localhost:" + port + "/api?name=Servlet";

        System.out.println("Tomcat started: " + url);
        tomcat.getServer().await();
    }

    private static Tomcat startTomcat(int port) throws Exception {
        Path baseDirectory = Files.createTempDirectory("servlet-tomcat-");

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDirectory.toString());
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", baseDirectory.toString());
        context.setParentClassLoader(ServletTomcatApiDemo.class.getClassLoader());
        if (context instanceof StandardContext standardContext) {
            standardContext.setClearReferencesThreadLocals(false);
            standardContext.setClearReferencesRmiTargets(false);
        }
        Tomcat.addServlet(context, "apiServlet", new ApiServlet());
        context.addServletMappingDecoded("/api", "apiServlet");
        addRequestLoggingFilter(context);
        context.addApplicationListener(DemoApplicationListener.class.getName());

        tomcat.start();
        return tomcat;
    }

    private static void addRequestLoggingFilter(Context context) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("requestLoggingFilter");
        filterDef.setFilter(new RequestLoggingFilter());
        context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("requestLoggingFilter");
        filterMap.addURLPatternDecoded("/*");
        context.addFilterMap(filterMap);
    }

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

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            doGet(request, response);
        }
    }

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
}
