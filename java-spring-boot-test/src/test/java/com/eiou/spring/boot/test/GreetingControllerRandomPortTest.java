package com.eiou.spring.boot.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = "debug=false",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(ServerSocketAvailableCondition.class)
class GreetingControllerRandomPortTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void callsApplicationThroughRandomPortHttpServer() {
        ResponseEntity<GreetingResponse> response = restTemplate.getForEntity(
                "/test-demo/hello?name=Http",
                GreetingResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(
                new GreetingResponse("hello from application.yml, Http", "hello from application.yml")
        );
    }
}
