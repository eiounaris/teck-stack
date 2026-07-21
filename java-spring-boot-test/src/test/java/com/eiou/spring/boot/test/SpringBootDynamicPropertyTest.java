package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "debug=false")
class SpringBootDynamicPropertyTest {
    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("demo.greeting.message", () -> "hello from dynamic property");
    }

    @Autowired
    private GreetingProperties properties;

    @Test
    void appliesDynamicPropertiesBeforeContextRefresh() {
        assertThat(properties.getMessage()).isEqualTo("hello from dynamic property");
    }
}
