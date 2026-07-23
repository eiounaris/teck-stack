package com.eiou.mall.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(max = 128)
        String name,

        @Size(max = 512)
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock,

        @Min(0)
        @Max(1)
        Integer status
) {
}
