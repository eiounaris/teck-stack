package com.eiou.spring.core;

import java.util.UUID;

public final class PrototypeCounter {
    private final String id = UUID.randomUUID().toString();

    public String id() {
        return id;
    }
}
