package com.eiou.spring.eventsasync;

import java.time.Instant;

public record OrderCompletedEvent(String orderNo, Instant completedAt) {
}
