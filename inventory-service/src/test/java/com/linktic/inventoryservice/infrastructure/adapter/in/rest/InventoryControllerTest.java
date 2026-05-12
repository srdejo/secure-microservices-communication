package com.linktic.inventoryservice.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.inventoryservice.application.usecase.InventoryUseCase;
import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.InventoryWithProduct;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.infrastructure.adapter.in.rest.dto.*;
import com.linktic.inventoryservice.infrastructure.adapter.mapper.InventoryMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private InventoryUseCase inventoryUseCase;

    @MockitoBean
    private InventoryMapper inventoryMapper;

    @Test
    void getInventoryByProductId() throws Exception {
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory(productId, 10);
        ProductInfo productInfo = new ProductInfo(productId, "Laptop", BigDecimal.TEN);
        InventoryWithProduct item = new InventoryWithProduct(inventory, productInfo);
        
        Mockito.when(inventoryUseCase.getInventoryByProductId(productId)).thenReturn(Optional.of(item));
        Mockito.when(inventoryMapper.toInventoryResponse(any(), any())).thenReturn(new JsonApiResponseInventoryDTO());

        mockMvc.perform(get("/api/v1/inventory/{productId}", productId))
                .andExpect(status().isOk());
    }

    @Test
    void updateInventory() throws Exception {
        UUID productId = UUID.randomUUID();
        UpdateInventoryQuantityRequestDTO request = new UpdateInventoryQuantityRequestDTO();
        request.setQuantity(5);

        Inventory inventory = new Inventory(productId, 5);
        Mockito.when(inventoryUseCase.updateInventory(eq(productId), eq(5))).thenReturn(inventory);
        Mockito.when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(new InventoryDTO());

        mockMvc.perform(patch("/api/v1/inventory/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void createPurchase() throws Exception {
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setProductId(UUID.randomUUID());
        request.setQuantity(2);

        Purchase purchase = new Purchase(UUID.randomUUID(), request.getProductId(), "Laptop", 2, BigDecimal.TEN, LocalDateTime.now());
        Mockito.when(inventoryUseCase.processPurchase(any(), any())).thenReturn(purchase);
        Mockito.when(inventoryMapper.toJsonResponse(any(Purchase.class))).thenReturn(new JsonApiResponsePurchaseDTO());

        mockMvc.perform(post("/api/v1/inventory/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void getAllInventory() throws Exception {
        Mockito.when(inventoryUseCase.getAllInventoryWithProducts()).thenReturn(java.util.List.of());
        Mockito.when(inventoryMapper.toInventoryListResponse(any(), any())).thenReturn(new JsonApiResponseInventoryListDTO());

        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk());
    }

    @Test
    void getPurchaseHistory() throws Exception {
        Mockito.when(inventoryUseCase.getPurchaseHistory()).thenReturn(java.util.List.of());
        Mockito.when(inventoryMapper.toPurchaseListResponse(any())).thenReturn(new JsonApiResponsePurchaseListDTO());

        mockMvc.perform(get("/api/v1/inventory/purchases"))
                .andExpect(status().isOk());
    }
}
