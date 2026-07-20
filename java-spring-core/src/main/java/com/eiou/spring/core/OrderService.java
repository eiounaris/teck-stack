package com.eiou.spring.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    public OrderService(OrderRepository orderRepository,
                        @Qualifier("wechatPaymentGateway") PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }

    public String placeOrder(String orderNo) {
        return orderRepository.save(orderNo) + ", " + paymentGateway.pay(orderNo);
    }
}
