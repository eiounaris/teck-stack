package com.eiou.spring.core;

public final class NamedPaymentGateway implements PaymentGateway {
    private final String name;

    public NamedPaymentGateway(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String pay(String orderNo) {
        return name + " paid " + orderNo;
    }
}
