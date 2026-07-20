package com.eiou.spring.mvc;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SpringMvcApiDemo {
    private static final int DEFAULT_PORT = 8080;

    private SpringMvcApiDemo() {
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("port", DEFAULT_PORT);
        Tomcat tomcat = startTomcat(port);

        System.out.println("Spring MVC started: http://localhost:" + port + "/mvc/hello?name=SpringMVC");
        tomcat.getServer().await();
    }

    private static Tomcat startTomcat(int port) throws Exception {
        Path baseDirectory = Files.createTempDirectory("spring-mvc-");

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDirectory.toString());
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", baseDirectory.toString());
        context.setParentClassLoader(SpringMvcApiDemo.class.getClassLoader());
        if (context instanceof StandardContext standardContext) {
            standardContext.setClearReferencesThreadLocals(false);
            standardContext.setClearReferencesRmiTargets(false);
        }

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(SpringMvcConfig.class);
        webApplicationContext.setServletContext(context.getServletContext());

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);

        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcherServlet");

        tomcat.start();
        return tomcat;
    }
}
