package com.eiou.spring.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mvc")
public class BasicController {
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "Spring MVC") String name) {
        return "hello " + name;
    }

    @GetMapping("/path/{value}")
    public String pathVariable(@PathVariable("value") String value) {
        return "pathVariable=" + value;
    }

    @GetMapping("/header")
    public ResponseEntity<String> requestHeader(@RequestHeader(name = "X-Demo", defaultValue = "missing") String demo) {
        return ResponseEntity
                .ok()
                .header("X-Spring-Mvc-Demo", "header")
                .body("X-Demo=" + demo);
    }

    @PostMapping(path = "/body", consumes = "text/plain")
    public String requestBody(@RequestBody String body) {
        return "body=" + body;
    }

    @GetMapping("/error")
    public String error() {
        throw new IllegalArgumentException("demo exception");
    }

    @GetMapping("/advice-error")
    public String adviceError() {
        throw new IllegalStateException("global advice exception");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("bad request: " + exception.getMessage());
    }
}
