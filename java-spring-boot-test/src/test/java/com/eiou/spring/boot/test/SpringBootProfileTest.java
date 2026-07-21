package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "debug=false")
@ActiveProfiles("test")
class SpringBootProfileTest {
    @Autowired
    private GreetingProperties properties;

    @Test
    void loadsProfileSpecificTestYaml() {
        assertThat(properties.getMessage()).isEqualTo("hello from application-test.yml");
    }
}
