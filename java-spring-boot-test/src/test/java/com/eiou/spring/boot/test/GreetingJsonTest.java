package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest(properties = "debug=false")
class GreetingJsonTest {
    @Autowired
    private JacksonTester<GreetingResponse> json;

    @Test
    void serializesResponseWithJsonSlice() throws Exception {
        JsonContent<GreetingResponse> content = json.write(
                new GreetingResponse("hello Json", "json")
        );

        assertThat(content).extractingJsonPathStringValue("$.greeting").isEqualTo("hello Json");
        assertThat(content).extractingJsonPathStringValue("$.configuredMessage").isEqualTo("json");
    }
}
