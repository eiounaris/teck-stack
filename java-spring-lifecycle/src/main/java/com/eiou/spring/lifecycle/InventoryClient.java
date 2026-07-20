package com.eiou.spring.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient implements ApplicationContextAware, InitializingBean, DisposableBean {
    private ApplicationContext applicationContext;

    public InventoryClient() {
        System.out.println("2. constructor: InventoryClient");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("3. ApplicationContextAware receives: " + applicationContext.getClass().getSimpleName());
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("5. @PostConstruct initializes remote client");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("6. InitializingBean.afterPropertiesSet validates required properties");
    }

    public String reserve(String sku) {
        return "reserved " + sku + " with " + applicationContext.getBeanDefinitionCount() + " bean definitions";
    }

    @PreDestroy
    void preDestroy() {
        System.out.println("8. @PreDestroy closes remote client resources");
    }

    @Override
    public void destroy() {
        System.out.println("9. DisposableBean.destroy releases fallback resources");
    }
}
