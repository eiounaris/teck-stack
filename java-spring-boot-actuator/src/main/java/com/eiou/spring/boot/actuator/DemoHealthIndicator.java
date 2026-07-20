package com.eiou.spring.boot.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DemoHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up()
                .withDetail("module", "java-spring-boot-actuator")
                .withDetail("check", "demo-health-indicator")
                .build();
    }
}
