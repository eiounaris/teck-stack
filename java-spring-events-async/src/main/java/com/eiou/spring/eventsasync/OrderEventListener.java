package com.eiou.spring.eventsasync;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    @EventListener
    public void writeAuditLog(OrderCompletedEvent event) {
        System.out.println("[event-sync] audit order=" + event.orderNo()
                + ", thread=" + Thread.currentThread().getName());
    }

    @Async
    @EventListener
    public void sendReceipt(OrderCompletedEvent event) throws InterruptedException {
        Thread.sleep(300);
        System.out.println("[event-async] receipt order=" + event.orderNo()
                + ", thread=" + Thread.currentThread().getName());
    }
}
