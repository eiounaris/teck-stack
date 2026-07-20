package com.eiou.spring.jdbctx;

import java.math.BigDecimal;

public record LedgerEntry(long id, String orderNo, String entryType, BigDecimal amount) {
}
