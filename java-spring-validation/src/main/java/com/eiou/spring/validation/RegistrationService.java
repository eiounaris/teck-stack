package com.eiou.spring.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RegistrationService {
    public String register(@Valid SignUpRequest request, @NotBlank String operator) {
        return operator + " registered " + request.getUsername();
    }
}
