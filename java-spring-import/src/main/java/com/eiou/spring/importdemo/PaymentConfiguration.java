package com.eiou.spring.importdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfiguration {
    @Bean
    ImportedPaymentService importedPaymentService() {
        return new ImportedPaymentService("imported payment config");
    }
}
