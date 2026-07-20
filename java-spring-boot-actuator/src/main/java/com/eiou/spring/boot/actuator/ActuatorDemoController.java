package com.eiou.spring.boot.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/actuator-demo")
public class ActuatorDemoController {
    private final Counter pingCounter;

    public ActuatorDemoController(MeterRegistry meterRegistry) {
        this.pingCounter = Counter.builder("demo.ping.count")
                .description("Counts demo ping requests")
                .tag("module", "java-spring-boot-actuator")
                .register(meterRegistry);
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        pingCounter.increment();
        return Map.of("status", "ok");
    }

    @GetMapping("/count")
    public Map<String, Object> count() {
        return Map.of("demo.ping.count", pingCounter.count());
    }
}
