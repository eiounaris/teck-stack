package com.eiou.spring.core;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan(basePackageClasses = SpringCoreConfig.class)
public class SpringCoreConfig {
    @Bean
    @Primary
    PaymentGateway alipayPaymentGateway() {
        return new NamedPaymentGateway("alipay");
    }

    @Bean
    PaymentGateway wechatPaymentGateway() {
        return new NamedPaymentGateway("wechat");
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PrototypeCounter prototypeCounter() {
        return new PrototypeCounter();
    }
}
