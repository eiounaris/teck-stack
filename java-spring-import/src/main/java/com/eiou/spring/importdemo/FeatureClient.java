package com.eiou.spring.importdemo;

public class FeatureClient {
    private final String mode;

    public FeatureClient(String mode) {
        this.mode = mode;
    }

    public String describe() {
        return this.mode + " feature enabled by ImportSelector";
    }
}
