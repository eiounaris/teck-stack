package com.eiou.spring.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringAopApiDemo {
    private SpringAopApiDemo() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringAopConfig.class)) {
            BillingService billingService = context.getBean(BillingService.class);
            System.out.println("proxy class: " + billingService.getClass().getName());

            System.out.println(billingService.charge("ORDER-1001", 8800));
            System.out.println(billingService.sensitiveOperation("direct proxy call"));

            System.out.println("self invocation starts");
            System.out.println(billingService.selfInvocationDoesNotTriggerAround());

            System.out.println("proxy invocation starts");
            System.out.println(billingService.proxyInvocationTriggersAround());
        }
    }
}
