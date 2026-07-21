package com.eiou.spring.boot.test;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    private final GreetingProperties properties;

    public GreetingService(GreetingProperties properties) {
        this.properties = properties;
    }

    public GreetingResponse greet(String name) {
        String normalizedName = (name == null || name.isBlank()) ? "Spring Boot Test" : name.trim();
        String configuredMessage = properties.getMessage();
        return new GreetingResponse(configuredMessage + ", " + normalizedName, configuredMessage);
    }
}
