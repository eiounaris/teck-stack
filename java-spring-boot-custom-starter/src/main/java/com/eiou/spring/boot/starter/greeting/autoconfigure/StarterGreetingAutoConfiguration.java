package com.eiou.spring.boot.starter.greeting.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(StarterGreetingService.class)
@EnableConfigurationProperties(StarterGreetingProperties.class)
@ConditionalOnProperty(
        prefix = "demo.starter.greeting",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class StarterGreetingAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    StarterGreetingService starterGreetingService(StarterGreetingProperties properties) {
        return new StarterGreetingService(properties);
    }
}
