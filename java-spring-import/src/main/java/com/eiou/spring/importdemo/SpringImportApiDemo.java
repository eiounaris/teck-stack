package com.eiou.spring.importdemo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringImportApiDemo {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringImportConfig.class)) {
            ImportedPaymentService paymentService = context.getBean(ImportedPaymentService.class);
            AuditClient auditClient = context.getBean(AuditClient.class);
            FeatureClient featureClient = context.getBean(FeatureClient.class);
            ImportFeatureReport featureReport = context.getBean(ImportFeatureReport.class);

            System.out.println("@Import configuration class: " + paymentService.receipt("ORDER-1001"));
            System.out.println("@Import regular class: " + auditClient.audit("startup"));
            System.out.println("ImportSelector: " + featureClient.describe());
            System.out.println("ImportBeanDefinitionRegistrar: " + featureReport.summary());
        }
    }
}
