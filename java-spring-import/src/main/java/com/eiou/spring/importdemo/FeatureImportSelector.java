package com.eiou.spring.importdemo;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class FeatureImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(
                EnableImportDemoFeatures.class.getName()
        );
        String mode = attributes == null ? "" : (String) attributes.get("mode");

        if ("cache".equalsIgnoreCase(mode)) {
            return new String[]{CacheFeatureConfiguration.class.getName()};
        }
        if ("messaging".equalsIgnoreCase(mode)) {
            return new String[]{MessagingFeatureConfiguration.class.getName()};
        }
        return new String[0];
    }
}
