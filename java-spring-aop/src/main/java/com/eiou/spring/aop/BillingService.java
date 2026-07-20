package com.eiou.spring.aop;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

@Service
public class BillingService {
    public String charge(String orderNo, int amountInCents) {
        return "charged " + orderNo + " amountInCents=" + amountInCents;
    }

    public String sensitiveOperation(String source) {
        return "sensitive operation executed from " + source;
    }

    public String selfInvocationDoesNotTriggerAround() {
        return this.sensitiveOperation("this.sensitiveOperation");
    }

    public String proxyInvocationTriggersAround() {
        BillingService proxy = (BillingService) AopContext.currentProxy();
        return proxy.sensitiveOperation("AopContext.currentProxy");
    }
}
