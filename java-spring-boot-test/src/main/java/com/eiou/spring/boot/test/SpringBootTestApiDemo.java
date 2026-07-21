package com.eiou.spring.boot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootTestApiDemo {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApiDemo.class, args);
    }
}
