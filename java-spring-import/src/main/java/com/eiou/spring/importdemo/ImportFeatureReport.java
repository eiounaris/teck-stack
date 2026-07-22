package com.eiou.spring.importdemo;

public class ImportFeatureReport {
    private final String mode;
    private final boolean metrics;

    public ImportFeatureReport(String mode, boolean metrics) {
        this.mode = mode;
        this.metrics = metrics;
    }

    public String summary() {
        return "mode=" + this.mode + ", metrics=" + this.metrics + " registered by ImportBeanDefinitionRegistrar";
    }
}
