package com.eiou.spring.jdbctx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public final class SpringJdbcTxApiDemo {
    private SpringJdbcTxApiDemo() {
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringJdbcTxConfig.class)) {
            LedgerService ledgerService = context.getBean(LedgerService.class);
            ledgerService.resetSchema();

            ledgerService.recordPayment("ORDER-1001", new BigDecimal("88.00"));

            try {
                ledgerService.recordPaymentAndFail("ORDER-1002", new BigDecimal("99.00"));
            } catch (IllegalStateException exception) {
                System.out.println("rollback example: " + exception.getMessage());
            }

            ledgerService.recordPaymentWithTransactionTemplate("ORDER-1003", new BigDecimal("66.00"));

            ledgerService.findEntries().forEach(System.out::println);
            System.out.println("entry count after rollback: " + ledgerService.countEntries());
        }
    }
}
