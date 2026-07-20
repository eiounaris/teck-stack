package com.eiou.spring.eventsasync;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OrderWorkflow {
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderWorkflow(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void completeOrder(String orderNo) {
        System.out.println("complete order: " + orderNo);
        applicationEventPublisher.publishEvent(new OrderCompletedEvent(orderNo, Instant.now()));
    }
}
