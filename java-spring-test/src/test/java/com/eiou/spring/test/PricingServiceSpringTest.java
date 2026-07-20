package com.eiou.spring.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfig.class)
class PricingServiceSpringTest {
    @Autowired
    private PricingService pricingService;

    @Test
    void injectsSpringBeanIntoJunitTest() {
        assertNotNull(pricingService);

        BigDecimal result = pricingService.applyDiscount(
                new BigDecimal("100.00"),
                new BigDecimal("0.15")
        );

        assertEquals(new BigDecimal("85.00"), result);
    }
}
