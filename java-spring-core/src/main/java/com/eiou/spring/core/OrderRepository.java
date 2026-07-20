package com.eiou.spring.core;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    public String save(String orderNo) {
        return "saved " + orderNo;
    }
}
