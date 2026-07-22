package com.eiou.knife4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "knife4j-demo", description = "Knife4j basic API")
@RestController
@RequestMapping("/knife4j-demo")
public class Knife4jDemoController {
    @Operation(summary = "Return a simple greeting")
    @GetMapping("/hello")
    public Map<String, String> hello(
            @Parameter(description = "Name shown in the greeting")
            @RequestParam(name = "name", defaultValue = "Knife4j") String name) {
        return Map.of("message", "hello " + name);
    }
}
