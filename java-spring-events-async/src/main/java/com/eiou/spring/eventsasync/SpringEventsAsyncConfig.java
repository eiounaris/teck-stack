package com.eiou.spring.eventsasync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan(basePackageClasses = SpringEventsAsyncConfig.class)
public class SpringEventsAsyncConfig {
    @Bean
    Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("spring-async-");
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.initialize();
        return executor;
    }

    @Bean
    TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("spring-scheduled-");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }
}
