package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "debug=false")
@AutoConfigureMockMvc
class GreetingControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void callsControllerThroughMockMvcWithoutStartingRealServer() throws Exception {
        mockMvc.perform(get("/test-demo/hello").param("name", "MockMvc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("hello from application.yml, MockMvc"))
                .andExpect(jsonPath("$.configuredMessage").value("hello from application.yml"));
    }
}
