package com.eiou.spring.boot;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApiDemo.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> System.out.println("Spring Boot application started: "
                + environment.getProperty("spring.application.name")
                + ", port="
                + environment.getProperty("server.port", "8080"));
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> System.out.println("CommandLineRunner args count: " + args.length);
    }
}
