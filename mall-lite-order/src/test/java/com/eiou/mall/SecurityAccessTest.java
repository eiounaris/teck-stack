package com.eiou.mall;

import com.eiou.mall.common.api.PageResponse;
import com.eiou.mall.product.dto.ProductResponse;
import com.eiou.mall.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    void currentUserRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void productCreateRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void productPageEndpointIsPublic() throws Exception {
        ProductResponse product = productResponse(1L, "Mechanical Keyboard");
        when(productService.page(eq(1L), eq(10L), eq(1), eq("Keyboard")))
                .thenReturn(PageResponse.of(1, 10, 1, List.of(product)));

        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "10")
                        .param("status", "1")
                        .param("keyword", "Keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.records[0].name").value("Mechanical Keyboard"));
    }

    @Test
    void productDetailEndpointIsPublic() throws Exception {
        when(productService.detail(1L)).thenReturn(productResponse(1L, "Mechanical Keyboard"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.name").value("Mechanical Keyboard"));
    }

    @Test
    void productOnSaleRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/products/1/on-sale"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void productOffSaleRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/products/1/off-sale"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void loginEndpointIsPublicButValidatesRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    private ProductResponse productResponse(Long id, String name) {
        return new ProductResponse(
                id,
                name,
                "Entry product for order flow testing.",
                new BigDecimal("299.00"),
                100,
                1,
                "ON_SALE",
                null,
                null
        );
    }
}
