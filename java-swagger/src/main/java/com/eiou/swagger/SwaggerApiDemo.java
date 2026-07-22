package com.eiou.swagger;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SwaggerApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(SwaggerApiDemo.class, args);
    }

    @Bean
    ApplicationRunner swaggerRunner(Environment environment) {
        return args -> {
            String port = environment.getProperty("server.port", "8080");
            String swaggerPath = environment.getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");
            System.out.println("Swagger UI started: http://localhost:" + port + swaggerPath);
        };
    }
}
