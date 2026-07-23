package com.eiou.mall.product.controller;

import com.eiou.mall.common.api.ApiResponse;
import com.eiou.mall.common.api.PageResponse;
import com.eiou.mall.product.dto.CreateProductRequest;
import com.eiou.mall.product.dto.ProductResponse;
import com.eiou.mall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product")
@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create product")
    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.ok(productService.create(request));
    }

    @Operation(summary = "Get product detail")
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.detail(id));
    }

    @Operation(summary = "Page products")
    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> page(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(productService.page(page, size, status, keyword));
    }

    @Operation(summary = "Put product on sale")
    @PutMapping("/{id}/on-sale")
    public ApiResponse<ProductResponse> onSale(@PathVariable Long id) {
        return ApiResponse.ok(productService.onSale(id));
    }

    @Operation(summary = "Put product off sale")
    @PutMapping("/{id}/off-sale")
    public ApiResponse<ProductResponse> offSale(@PathVariable Long id) {
        return ApiResponse.ok(productService.offSale(id));
    }
}
