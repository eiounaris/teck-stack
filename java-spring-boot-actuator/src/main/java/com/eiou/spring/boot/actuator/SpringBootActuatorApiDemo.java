package com.eiou.spring.boot.actuator;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SpringBootActuatorApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootActuatorApiDemo.class, args);
    }

    @Bean
    ApplicationRunner actuatorRunner(Environment environment) {
        return args -> {
            String port = environment.getProperty("server.port", "8080");
            String basePath = environment.getProperty("management.endpoints.web.base-path", "/actuator");
            System.out.println("Actuator started: http://localhost:" + port + basePath);
        };
    }
}
