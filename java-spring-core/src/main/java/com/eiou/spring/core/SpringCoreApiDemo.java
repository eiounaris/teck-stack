package com.eiou.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringCoreApiDemo {
    private SpringCoreApiDemo() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringCoreConfig.class)) {
            OrderService orderService = context.getBean(OrderService.class);
            System.out.println(orderService.placeOrder("ORDER-1001"));

            PaymentGateway primaryGateway = context.getBean(PaymentGateway.class);
            System.out.println("primary payment gateway: " + primaryGateway.name());

            PrototypeCounter firstCounter = context.getBean(PrototypeCounter.class);
            PrototypeCounter secondCounter = context.getBean(PrototypeCounter.class);
            System.out.println("prototype bean creates new instance: " + (firstCounter != secondCounter));

            OrderRepository firstRepository = context.getBean(OrderRepository.class);
            OrderRepository secondRepository = context.getBean(OrderRepository.class);
            System.out.println("singleton repository reused: " + (firstRepository == secondRepository));
        }
    }
}
