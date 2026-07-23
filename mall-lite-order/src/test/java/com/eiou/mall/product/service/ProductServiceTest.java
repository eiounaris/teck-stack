package com.eiou.mall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eiou.mall.common.api.PageResponse;
import com.eiou.mall.common.exception.BusinessException;
import com.eiou.mall.product.dto.CreateProductRequest;
import com.eiou.mall.product.dto.ProductResponse;
import com.eiou.mall.product.entity.MallProduct;
import com.eiou.mall.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createDefaultsToOnSale() {
        CreateProductRequest request = new CreateProductRequest(
                "USB-C Hub",
                "Useful hub",
                new BigDecimal("199.00"),
                80,
                null
        );
        when(productMapper.selectCount(any())).thenReturn(0L);
        when(productMapper.insert(any(MallProduct.class))).thenAnswer(invocation -> {
            MallProduct product = invocation.getArgument(0);
            product.setId(100L);
            return 1;
        });

        ProductResponse response = productService.create(request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(1);
        assertThat(response.statusText()).isEqualTo("ON_SALE");

        ArgumentCaptor<MallProduct> productCaptor = ArgumentCaptor.forClass(MallProduct.class);
        verify(productMapper).insert(productCaptor.capture());
        assertThat(productCaptor.getValue().getName()).isEqualTo("USB-C Hub");
        assertThat(productCaptor.getValue().getStock()).isEqualTo(80);
    }

    @Test
    void createRejectsDuplicateName() {
        CreateProductRequest request = new CreateProductRequest(
                "USB-C Hub",
                "Useful hub",
                new BigDecimal("199.00"),
                80,
                1
        );
        when(productMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product name already exists")
                .satisfies(exception -> assertThat(((BusinessException) exception).code()).isEqualTo("409"));
    }

    @Test
    void offSaleRejectsMissingProduct() {
        when(productMapper.selectById(404L)).thenReturn(null);

        assertThatThrownBy(() -> productService.offSale(404L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found")
                .satisfies(exception -> assertThat(((BusinessException) exception).code()).isEqualTo("404"));
    }

    @Test
    void pageMapsRecords() {
        MallProduct product = new MallProduct();
        product.setId(1L);
        product.setName("Mechanical Keyboard");
        product.setDescription("Keyboard");
        product.setPrice(new BigDecimal("299.00"));
        product.setStock(100);
        product.setStatus(1);

        Page<MallProduct> page = new Page<MallProduct>(1, 10)
                .setTotal(1)
                .setRecords(List.of(product));
        when(productMapper.selectPage(any(), any())).thenReturn(page);

        PageResponse<ProductResponse> response = productService.page(1, 10, 1, "Keyboard");

        assertThat(response.total()).isEqualTo(1);
        assertThat(response.pages()).isEqualTo(1);
        assertThat(response.records()).hasSize(1);
        assertThat(response.records().getFirst().name()).isEqualTo("Mechanical Keyboard");
        verify(productMapper).selectPage(any(), any());
    }

    @Test
    void onSaleUpdatesExistingProduct() {
        MallProduct product = new MallProduct();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setPrice(new BigDecimal("129.00"));
        product.setStock(200);
        product.setStatus(0);
        when(productMapper.selectById(eq(1L))).thenReturn(product);

        ProductResponse response = productService.onSale(1L);

        assertThat(response.status()).isEqualTo(1);
        assertThat(response.statusText()).isEqualTo("ON_SALE");
        verify(productMapper).updateById(product);
    }
}
