package com.eiou.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/security")
public class SecurityDemoController {
    @GetMapping("/public")
    public Map<String, String> publicApi() {
        return Map.of("message", "public api");
    }

    @GetMapping("/private")
    public Map<String, Object> privateApi(Authentication authentication) {
        return Map.of(
                "message", "authenticated api",
                "name", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
