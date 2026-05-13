package com.linktic.productservice.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.productservice.domain.port.ProductServicePort;
import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.infrastructure.adapter.in.rest.dto.ProductDTO;
import com.linktic.productservice.infrastructure.adapter.in.rest.dto.JsonApiResponseProductDTO;
import com.linktic.productservice.infrastructure.adapter.in.rest.dto.JsonApiResponseProductListDTO;
import com.linktic.productservice.infrastructure.adapter.mapper.ProductMapper;
import com.linktic.productservice.infrastructure.config.ApiKeyFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductServicePort productServicePort;

    @MockitoBean
    private ProductMapper productMapper;

    @MockitoBean
    private ApiKeyFilter apiKeyFilter;

    @Test
    void createProduct() throws Exception {
        UUID id = UUID.randomUUID();
        ProductDTO request = new ProductDTO();
        request.setName("Laptop");
        request.setPrice(1000.0);

        Product product = new Product(id, "Laptop", "Desc", new BigDecimal("1000.00"));
        Mockito.when(productServicePort.createProduct(any())).thenReturn(product);
        Mockito.when(productMapper.toJsonResponse(any(Product.class))).thenReturn(new JsonApiResponseProductDTO());

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void getProduct() throws Exception {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Laptop", "Desc", new BigDecimal("1000.00"));
        Mockito.when(productServicePort.getProductById(id)).thenReturn(Optional.of(product));
        Mockito.when(productMapper.toJsonResponse(any(Product.class))).thenReturn(new JsonApiResponseProductDTO());

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProducts() throws Exception {
        Mockito.when(productServicePort.getAllProducts()).thenReturn(List.of());
        Mockito.when(productMapper.toJsonResponse(any(List.class))).thenReturn(new JsonApiResponseProductListDTO());
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }
}
