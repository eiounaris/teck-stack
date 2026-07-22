package com.eiou.spring.boot.starter.demo;

import com.eiou.spring.boot.starter.greeting.autoconfigure.StarterGreetingService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomStarterDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomStarterDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner customStarterRunner(ApplicationContext applicationContext,
                                          StarterGreetingService greetingService) {
        return args -> {
            System.out.println(greetingService.greet("Custom Starter"));
            System.out.println("starterGreetingService bean exists: "
                    + applicationContext.containsBean("starterGreetingService"));
        };
    }
}
