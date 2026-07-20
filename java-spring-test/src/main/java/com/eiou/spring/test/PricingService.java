package com.eiou.spring.test;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {
    public BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountRate) {
        BigDecimal discount = amount.multiply(discountRate);
        return amount.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
}
