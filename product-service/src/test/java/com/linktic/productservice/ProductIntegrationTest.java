package com.linktic.productservice;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.domain.port.ProductPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.clean-disabled=false",
    "spring.jpa.hibernate.ddl-auto=update",
    "api.key=test-api-key"
})
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.linktic.productservice.domain.port.InventoryClientPort inventoryClientPort;

    @Autowired
    private ProductPersistencePort productPersistencePort;

    @Test
    void shouldCreateProduct() throws Exception {
        String productJson = "{\"name\": \"Integration Test Product\", \"price\": 99.99, \"description\": \"Test Description\"}";

        mockMvc.perform(post("/api/v1/products")
                        .header("X-API-KEY", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.data.attributes.price").value(99.99));
    }

    @Test
    void shouldGetProductById() throws Exception {
        UUID id = UUID.randomUUID();
        productPersistencePort.save(new Product(id, "Existing Product", "Desc", BigDecimal.valueOf(50.0)));

        mockMvc.perform(get("/api/v1/products/{id}", id)
                        .header("X-API-KEY", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.attributes.name").value("Existing Product"));
    }
}
