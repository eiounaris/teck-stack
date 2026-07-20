package com.eiou.spring.test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void resetSchema() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_order");
        jdbcTemplate.execute("""
                CREATE TABLE test_order (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_no VARCHAR(64) NOT NULL,
                    amount DECIMAL(12, 2) NOT NULL
                )
                """);
    }

    public void save(String orderNo, BigDecimal amount) {
        jdbcTemplate.update(
                "INSERT INTO test_order (order_no, amount) VALUES (?, ?)",
                orderNo,
                amount
        );
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_order", Integer.class);
        return count == null ? 0 : count;
    }
}
