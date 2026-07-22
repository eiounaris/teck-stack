package com.eiou.spring.importdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingFeatureConfiguration {
    @Bean
    FeatureClient featureClient() {
        return new FeatureClient("messaging");
    }
}
