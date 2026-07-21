package com.eiou.spring.validation;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan(basePackageClasses = SpringValidationConfig.class)
public class SpringValidationConfig {
    @Bean
    LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setMessageInterpolator(new ParameterMessageInterpolator());
        return validatorFactoryBean;
    }

    @Bean
    static MethodValidationPostProcessor methodValidationPostProcessor(
            ObjectProvider<jakarta.validation.Validator> validatorProvider) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidatorProvider(validatorProvider);
        return postProcessor;
    }
}
