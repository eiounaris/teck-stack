package com.eiou.spring.boot.starter.greeting.autoconfigure;

public class StarterGreetingService {
    private final StarterGreetingProperties properties;

    public StarterGreetingService(StarterGreetingProperties properties) {
        this.properties = properties;
    }

    public String greet(String name) {
        String normalizedName = (name == null || name.isBlank()) ? "Spring Boot" : name.trim();
        return properties.getPrefix() + " " + normalizedName + " " + properties.getSuffix();
    }
}
