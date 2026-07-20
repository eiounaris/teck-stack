package com.eiou.spring.jdbctx;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LedgerService {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public LedgerService(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void resetSchema() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS ledger_entry");
        jdbcTemplate.execute("""
                CREATE TABLE ledger_entry (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_no VARCHAR(64) NOT NULL,
                    entry_type VARCHAR(16) NOT NULL,
                    amount DECIMAL(12, 2) NOT NULL
                )
                """);
    }

    @Transactional
    public void recordPayment(String orderNo, BigDecimal amount) {
        insertEntry(orderNo, "DEBIT", amount);
        insertEntry(orderNo, "CREDIT", amount.negate());
    }

    @Transactional
    public void recordPaymentAndFail(String orderNo, BigDecimal amount) {
        insertEntry(orderNo, "DEBIT", amount);
        insertEntry(orderNo, "CREDIT", amount.negate());
        throw new IllegalStateException("runtime exception rolls back ORDER-1002");
    }

    public void recordPaymentWithTransactionTemplate(String orderNo, BigDecimal amount) {
        transactionTemplate.executeWithoutResult(status -> {
            insertEntry(orderNo, "DEBIT", amount);
            insertEntry(orderNo, "CREDIT", amount.negate());
        });
    }

    public List<LedgerEntry> findEntries() {
        return jdbcTemplate.query(
                "SELECT id, order_no, entry_type, amount FROM ledger_entry ORDER BY id",
                (resultSet, rowNum) -> new LedgerEntry(
                        resultSet.getLong("id"),
                        resultSet.getString("order_no"),
                        resultSet.getString("entry_type"),
                        resultSet.getBigDecimal("amount")
                )
        );
    }

    public int countEntries() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ledger_entry", Integer.class);
        return count == null ? 0 : count;
    }

    private void insertEntry(String orderNo, String entryType, BigDecimal amount) {
        jdbcTemplate.update(
                "INSERT INTO ledger_entry (order_no, entry_type, amount) VALUES (?, ?, ?)",
                orderNo,
                entryType,
                amount
        );
    }
}
