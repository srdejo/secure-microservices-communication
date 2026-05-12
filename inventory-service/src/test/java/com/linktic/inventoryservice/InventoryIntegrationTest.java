package com.linktic.inventoryservice;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.port.InventoryPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryPersistencePort inventoryPersistencePort;

    @MockitoBean
    private com.linktic.inventoryservice.domain.port.ProductClientPort productClientPort;

    @Test
    void shouldReturnInventory_WhenExists() throws Exception {
        UUID productId = UUID.randomUUID();
        inventoryPersistencePort.save(new Inventory(productId, 50));
        
        // Mock product service to return the product info, otherwise controller returns 404
        var productInfo = new com.linktic.inventoryservice.domain.model.ProductInfo(
            productId, "Test Product", "Test Description", java.math.BigDecimal.valueOf(100.0)
        );
        org.mockito.Mockito.when(productClientPort.getProductById(productId))
                .thenReturn(java.util.Optional.of(productInfo));

        mockMvc.perform(get("/api/v1/inventory/{productId}", productId)
                        .header("X-API-KEY", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(50))
                .andExpect(jsonPath("$.data.id").value(productId.toString()));
    }

    @Test
    void shouldProcessPurchase_WhenInventoryExists() throws Exception {
        UUID productId = UUID.randomUUID();
        inventoryPersistencePort.save(new Inventory(productId, 10));

        // Mock external product service response
        var productInfo = new com.linktic.inventoryservice.domain.model.ProductInfo(
            productId, "Test Product", "Test Description", java.math.BigDecimal.valueOf(100.0)
        );
        org.mockito.Mockito.when(productClientPort.getProductById(productId))
                .thenReturn(java.util.Optional.of(productInfo));

        String purchaseJson = "{\"productId\": \"" + productId + "\", \"quantity\": 2}";

        mockMvc.perform(post("/api/v1/inventory/purchase")
                        .header("X-API-KEY", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attributes.totalPrice").value(200.0))
                .andExpect(jsonPath("$.data.attributes.productDescription").value("Test Description"))
                .andExpect(jsonPath("$.data.attributes.quantity").value(2));
    }

    @Test
    void shouldReturn401_WhenApiKeyMissing() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{productId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
