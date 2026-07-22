package com.eiou.spring.importdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheFeatureConfiguration {
    @Bean
    FeatureClient featureClient() {
        return new FeatureClient("cache");
    }
}
