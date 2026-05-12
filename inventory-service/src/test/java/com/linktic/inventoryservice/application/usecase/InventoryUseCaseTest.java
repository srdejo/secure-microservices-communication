package com.linktic.inventoryservice.application.usecase;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.domain.port.InventoryPersistencePort;
import com.linktic.inventoryservice.domain.port.ProductClientPort;
import com.linktic.inventoryservice.domain.port.PurchasePersistencePort;
import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryUseCaseTest {

    @Mock
    private InventoryPersistencePort persistencePort;
    @Mock
    private PurchasePersistencePort purchasePersistencePort;
    @Mock
    private ProductClientPort clientPort;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryUseCase inventoryUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processPurchase_ShouldSuccess_WhenStockAvailable() {
        UUID productId = UUID.randomUUID();
        ProductInfo product = new ProductInfo(productId, "Test Product", BigDecimal.TEN);

        Inventory inventory = new Inventory(productId, 10);
        Purchase purchase = new Purchase(UUID.randomUUID(), productId, "Test Product", 5, BigDecimal.valueOf(50), LocalDateTime.now());

        when(clientPort.getProductById(productId)).thenReturn(Optional.of(product));
        when(persistencePort.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(purchasePersistencePort.save(any(Purchase.class))).thenReturn(purchase);

        Purchase result = inventoryUseCase.processPurchase(productId, 5);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(5, inventory.getQuantity());
        verify(persistencePort, times(1)).save(inventory);
        verify(purchasePersistencePort, times(1)).save(any(Purchase.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void processPurchase_ShouldThrowException_WhenStockInsufficient() {
        UUID productId = UUID.randomUUID();
        ProductInfo product = new ProductInfo(productId, "Test Product", BigDecimal.TEN);

        Inventory inventory = new Inventory(productId, 2);

        when(clientPort.getProductById(productId)).thenReturn(Optional.of(product));
        when(persistencePort.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> inventoryUseCase.processPurchase(productId, 5));
    }
}
