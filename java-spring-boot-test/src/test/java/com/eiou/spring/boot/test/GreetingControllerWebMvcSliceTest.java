package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GreetingController.class, properties = "debug=false")
class GreetingControllerWebMvcSliceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GreetingService greetingService;

    @Test
    void loadsMvcSliceAndMocksControllerCollaborator() throws Exception {
        given(greetingService.greet("Slice"))
                .willReturn(new GreetingResponse("hello from mocked service, Slice", "mocked"));

        mockMvc.perform(get("/test-demo/hello").param("name", "Slice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("hello from mocked service, Slice"))
                .andExpect(jsonPath("$.configuredMessage").value("mocked"));
    }
}
