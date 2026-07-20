package com.eiou.spring.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SpringLifecycleConfig.class)
public class SpringLifecycleConfig {
    @Bean
    static BeanFactoryPostProcessor lifecycleBeanFactoryPostProcessor() {
        return beanFactory -> {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("inventoryClient");
            beanDefinition.setDescription("Inventory client touched by BeanFactoryPostProcessor");
            System.out.println("1. BeanFactoryPostProcessor updates inventoryClient BeanDefinition");
        };
    }

    @Bean
    static BeanPostProcessor lifecycleBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if ("inventoryClient".equals(beanName)) {
                    System.out.println("4. BeanPostProcessor before initialization: " + beanName);
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if ("inventoryClient".equals(beanName)) {
                    System.out.println("7. BeanPostProcessor after initialization: " + beanName);
                }
                return bean;
            }
        };
    }
}
