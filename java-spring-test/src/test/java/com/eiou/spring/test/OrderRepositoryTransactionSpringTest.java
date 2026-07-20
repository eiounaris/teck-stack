package com.eiou.spring.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRepositoryTransactionSpringTest {
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.resetSchema();
    }

    @Test
    @Order(1)
    @Transactional
    void transactionalTestRollsBackByDefault() {
        orderRepository.save("ORDER-1001", new BigDecimal("88.00"));

        assertEquals(1, orderRepository.count());
    }

    @Test
    @Order(2)
    void nextTestStartsWithCleanDatabase() {
        assertEquals(0, orderRepository.count());
    }
}
