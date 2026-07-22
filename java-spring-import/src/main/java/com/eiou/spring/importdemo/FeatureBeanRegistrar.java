package com.eiou.spring.importdemo;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class FeatureBeanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(
                EnableImportDemoFeatures.class.getName()
        );
        String mode = attributes == null ? "unknown" : (String) attributes.get("mode");
        boolean metrics = attributes != null && (Boolean) attributes.get("metrics");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(ImportFeatureReport.class)
                .addConstructorArgValue(mode)
                .addConstructorArgValue(metrics);

        registry.registerBeanDefinition("importFeatureReport", builder.getBeanDefinition());
    }
}
