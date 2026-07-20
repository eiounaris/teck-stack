package com.eiou.spring.boot.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DemoInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("demo", Map.of(
                "module", "java-spring-boot-actuator",
                "purpose", "Spring Boot Actuator quick reference",
                "endpoints", "health,info,metrics,beans,conditions,loggers"
        ));
    }
}
