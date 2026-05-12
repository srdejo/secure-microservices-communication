package com.linktic.inventoryservice;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.port.InventoryPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class InventoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("api.key", () -> "test-api-key");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryPersistencePort inventoryPersistencePort;

    @Test
    void shouldReturnInventory_WhenExists() throws Exception {
        UUID productId = UUID.randomUUID();
        inventoryPersistencePort.save(new Inventory(productId, 50));

        mockMvc.perform(get("/api/v1/inventory/{productId}", productId)
                        .header("X-API-KEY", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(50))
                .andExpect(jsonPath("$.data.id").value(productId.toString()));
    }

    @Test
    void shouldReturn401_WhenApiKeyMissing() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{productId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
