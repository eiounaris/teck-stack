package com.eiou.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eiou.mall.common.api.PageResponse;
import com.eiou.mall.common.exception.BusinessException;
import com.eiou.mall.common.exception.ErrorCode;
import com.eiou.mall.product.dto.CreateProductRequest;
import com.eiou.mall.product.dto.ProductResponse;
import com.eiou.mall.product.entity.MallProduct;
import com.eiou.mall.product.mapper.ProductMapper;
import com.eiou.mall.product.model.ProductStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Long existing = productMapper.selectCount(Wrappers.<MallProduct>lambdaQuery()
                .eq(MallProduct::getName, request.name()));
        if (existing > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Product name already exists");
        }

        ProductStatus status = ProductStatus.fromCode(request.status());
        MallProduct product = new MallProduct();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setStatus(status.code());
        productMapper.insert(product);
        return toResponse(product);
    }

    public ProductResponse detail(Long id) {
        return toResponse(findById(id));
    }

    public PageResponse<ProductResponse> page(long page, long size, Integer status, String keyword) {
        LambdaQueryWrapper<MallProduct> query = Wrappers.lambdaQuery();
        if (status != null) {
            query.eq(MallProduct::getStatus, ProductStatus.fromCode(status).code());
        }
        if (StringUtils.hasText(keyword)) {
            query.like(MallProduct::getName, keyword.trim());
        }
        query.orderByDesc(MallProduct::getCreatedAt).orderByDesc(MallProduct::getId);

        Page<MallProduct> result = productMapper.selectPage(Page.of(page, size), query);
        List<ProductResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        return PageResponse.of(result.getCurrent(), result.getSize(), result.getTotal(), records);
    }

    @Transactional
    public ProductResponse onSale(Long id) {
        return changeStatus(id, ProductStatus.ON_SALE);
    }

    @Transactional
    public ProductResponse offSale(Long id) {
        return changeStatus(id, ProductStatus.OFF_SALE);
    }

    private ProductResponse changeStatus(Long id, ProductStatus status) {
        MallProduct product = findById(id);
        product.setStatus(status.code());
        productMapper.updateById(product);
        return toResponse(product);
    }

    private MallProduct findById(Long id) {
        MallProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Product not found");
        }
        return product;
    }

    private ProductResponse toResponse(MallProduct product) {
        ProductStatus status = ProductStatus.fromCode(product.getStatus());
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                status.text(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
