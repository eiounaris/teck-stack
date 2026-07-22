package com.eiou.spring.importdemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PaymentConfiguration.class, AuditClient.class})
@EnableImportDemoFeatures(mode = "cache", metrics = true)
public class SpringImportConfig {
}
