package com.eiou.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionalConfig {
    @Bean
    @ConditionalOnProperty(prefix = "demo.feature", name = "enabled", havingValue = "true")
    DemoFeature enabledDemoFeature(DemoProperties properties) {
        return () -> "enabled demo feature, mode=" + properties.getFeature().getMode();
    }

    @Bean
    @ConditionalOnMissingBean(DemoFeature.class)
    DemoFeature defaultDemoFeature() {
        return () -> "default demo feature from @ConditionalOnMissingBean";
    }
}
