package com.eiou.spring.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class SignUpRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpRequest request = (SignUpRequest) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.required", "username is required");

        if (request.getUsername() != null && request.getUsername().toLowerCase().contains("admin")) {
            errors.rejectValue("username", "username.reserved", "username must not contain admin");
        }
    }
}
