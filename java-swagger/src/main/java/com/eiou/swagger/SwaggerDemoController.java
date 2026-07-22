package com.eiou.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "swagger-demo", description = "Swagger/OpenAPI basic API")
@RestController
@RequestMapping("/swagger-demo")
public class SwaggerDemoController {
    @Operation(summary = "Return a simple greeting")
    @GetMapping("/hello")
    public Map<String, String> hello(
            @Parameter(description = "Name shown in the greeting")
            @RequestParam(name = "name", defaultValue = "Swagger") String name) {
        return Map.of("message", "hello " + name);
    }
}
