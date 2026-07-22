package com.eiou.spring.importdemo;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FeatureImportSelector.class, FeatureBeanRegistrar.class})
public @interface EnableImportDemoFeatures {
    String mode() default "cache";

    boolean metrics() default true;
}
