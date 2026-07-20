package com.eiou.spring.core;

public interface PaymentGateway {
    String name();

    String pay(String orderNo);
}
