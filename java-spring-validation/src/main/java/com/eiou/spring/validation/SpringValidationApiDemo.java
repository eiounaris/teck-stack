package com.eiou.spring.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

public class SpringValidationApiDemo {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SpringValidationConfig.class)) {
            validateWithDataBinder(context);
            validateWithBeanValidation(context);
            validateMethodArguments(context);
        }
    }

    private static void validateWithDataBinder(AnnotationConfigApplicationContext context) {
        SignUpRequest invalidRequest = SignUpRequest.invalid();
        SignUpRequestValidator springValidator = context.getBean(SignUpRequestValidator.class);
        LocalValidatorFactoryBean beanValidator = context.getBean(LocalValidatorFactoryBean.class);

        DataBinder dataBinder = new DataBinder(invalidRequest);
        dataBinder.addValidators(beanValidator, springValidator);
        dataBinder.validate();

        printBindingResult("DataBinder + Spring Validator + Bean Validation", dataBinder.getBindingResult());
    }

    private static void validateWithBeanValidation(AnnotationConfigApplicationContext context) {
        Validator validator = context.getBean(Validator.class);
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(SignUpRequest.invalid());

        System.out.println("\nBean Validation violations:");
        violations.forEach(violation -> System.out.println(
                "- " + violation.getPropertyPath() + ": " + violation.getMessage()
        ));
    }

    private static void validateMethodArguments(AnnotationConfigApplicationContext context) {
        RegistrationService registrationService = context.getBean(RegistrationService.class);

        System.out.println("\nMethod validation:");
        try {
            registrationService.register(SignUpRequest.valid(), "");
        } catch (ConstraintViolationException exception) {
            exception.getConstraintViolations().forEach(violation -> System.out.println(
                    "- " + violation.getPropertyPath() + ": " + violation.getMessage()
            ));
        }
    }

    private static void printBindingResult(String title, BindingResult bindingResult) {
        System.out.println("\n" + title + ":");
        bindingResult.getFieldErrors().forEach(error -> System.out.println(
                "- " + error.getField() + ": " + error.getDefaultMessage()
        ));
        bindingResult.getGlobalErrors().forEach(error -> System.out.println(
                "- " + error.getObjectName() + ": " + error.getDefaultMessage()
        ));
    }

    static BindingResult validateForTests(SignUpRequest request, SignUpRequestValidator springValidator) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "signUpRequest");
        springValidator.validate(request, bindingResult);
        return bindingResult;
    }
}
