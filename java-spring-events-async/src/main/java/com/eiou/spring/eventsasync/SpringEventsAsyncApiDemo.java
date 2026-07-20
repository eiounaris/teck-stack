package com.eiou.spring.eventsasync;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringEventsAsyncApiDemo {
    private SpringEventsAsyncApiDemo() {
    }

    public static void main(String[] args) throws InterruptedException {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringEventsAsyncConfig.class)) {
            OrderWorkflow orderWorkflow = context.getBean(OrderWorkflow.class);
            orderWorkflow.completeOrder("ORDER-1001");

            Thread.sleep(1500);
        }
    }
}
