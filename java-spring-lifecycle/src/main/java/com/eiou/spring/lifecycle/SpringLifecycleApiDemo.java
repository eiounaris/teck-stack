package com.eiou.spring.lifecycle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringLifecycleApiDemo {
    private SpringLifecycleApiDemo() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringLifecycleConfig.class)) {
            InventoryClient inventoryClient = context.getBean(InventoryClient.class);
            System.out.println(inventoryClient.reserve("SKU-1001"));
        }
    }
}
