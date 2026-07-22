package com.eiou.spring.importdemo;

public class AuditClient {
    public String audit(String action) {
        return "audit " + action + " through regular imported class";
    }
}
