package com.eiou.knife4j;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Knife4jApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(Knife4jApiDemo.class, args);
    }

    @Bean
    ApplicationRunner knife4jRunner(Environment environment) {
        return args -> {
            String port = environment.getProperty("server.port", "8080");
            System.out.println("Knife4j UI started: http://localhost:" + port + "/doc.html");
        };
    }
}
