package com.eiou.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ServletTomcatApiDemo {
    private static final int DEFAULT_PORT = 8080;

    private ServletTomcatApiDemo() {
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("port", DEFAULT_PORT);
        Tomcat tomcat = startTomcat(port);

        System.out.println("Tomcat started: http://localhost:" + port + "/api?name=Servlet");
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

        tomcat.start();
        return tomcat;
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
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            doGet(request, response);
        }
    }
}
