package com.eiou.spring.eventsasync;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class HeartbeatJob {
    private final AtomicInteger counter = new AtomicInteger();

    @Scheduled(fixedDelay = 500, initialDelay = 200)
    public void tick() {
        int value = counter.incrementAndGet();
        if (value <= 3) {
            System.out.println("[scheduled] tick=" + value + ", thread=" + Thread.currentThread().getName());
        }
    }
}
