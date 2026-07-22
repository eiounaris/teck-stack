package com.eiou.spring.importdemo;

public class ImportedPaymentService {
    private final String source;

    public ImportedPaymentService(String source) {
        this.source = source;
    }

    public String receipt(String orderNo) {
        return "paid " + orderNo + " by " + this.source;
    }
}
