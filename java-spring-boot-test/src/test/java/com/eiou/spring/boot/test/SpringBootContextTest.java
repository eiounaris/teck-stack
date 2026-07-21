package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "debug=false",
        "demo.greeting.message=hello from @SpringBootTest"
})
class SpringBootContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GreetingService greetingService;

    @Test
    void loadsFullContextAndAppliesInlineProperties() {
        assertThat(applicationContext.containsBean("greetingController")).isTrue();
        assertThat(greetingService.greet("Context").greeting())
                .isEqualTo("hello from @SpringBootTest, Context");
    }
}
