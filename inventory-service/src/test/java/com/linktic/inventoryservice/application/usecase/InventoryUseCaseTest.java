package com.linktic.inventoryservice.application.usecase;

import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.domain.port.InventoryPersistencePort;
import com.linktic.inventoryservice.domain.port.ProductClientPort;
import com.linktic.inventoryservice.domain.port.PurchasePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryUseCaseTest {

    @Mock
    private InventoryPersistencePort inventoryPersistencePort;

    @Mock
    private PurchasePersistencePort purchasePersistencePort;

    @Mock
    private ProductClientPort productClientPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryUseCase inventoryUseCase;

    private UUID productId;
    private ProductInfo sampleProduct;
    private Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = new ProductInfo(productId, "Test Product", new BigDecimal("50.00"));
        sampleInventory = new Inventory(productId, 10);
    }

    @Test
    void processPurchase_Success() {
        // Arrange
        when(productClientPort.getProductById(productId)).thenReturn(Optional.of(sampleProduct));
        when(inventoryPersistencePort.findByProductId(productId)).thenReturn(Optional.of(sampleInventory));
        when(purchasePersistencePort.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Purchase result = inventoryUseCase.processPurchase(productId, 2);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("100.00"), result.getTotalPrice());
        assertEquals(8, sampleInventory.getQuantity()); // Stock updated
        
        verify(inventoryPersistencePort, times(1)).save(sampleInventory);
        verify(purchasePersistencePort, times(1)).save(any(Purchase.class));
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void processPurchase_FailsWhenProductNotFound() {
        // Arrange
        when(productClientPort.getProductById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> inventoryUseCase.processPurchase(productId, 1));
        
        assertTrue(exception.getMessage().contains("Product not found"));
        verify(inventoryPersistencePort, never()).save(any());
    }

    @Test
    void processPurchase_FailsWhenInsufficientStock() {
        // Arrange
        when(productClientPort.getProductById(productId)).thenReturn(Optional.of(sampleProduct));
        when(inventoryPersistencePort.findByProductId(productId)).thenReturn(Optional.of(sampleInventory));

        // Act & Assert
        assertThrows(InsufficientStockException.class, 
                () -> inventoryUseCase.processPurchase(productId, 11));
        
        verify(inventoryPersistencePort, never()).save(any());
        verify(purchasePersistencePort, never()).save(any());
    }

    @Test
    void updateInventory_Success() {
        // Arrange
        when(inventoryPersistencePort.findByProductId(productId)).thenReturn(Optional.of(sampleInventory));
        when(inventoryPersistencePort.save(any(Inventory.class))).thenReturn(sampleInventory);

        // Act
        Inventory result = inventoryUseCase.updateInventory(productId, 20);

        // Assert
        assertEquals(20, result.getQuantity());
        verify(inventoryPersistencePort, times(1)).save(sampleInventory);
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    void getInventoryByProductId_ReturnsVirtualZeroWhenNotFoundInDbButProductExists() {
        // Arrange
        when(productClientPort.getProductById(productId)).thenReturn(Optional.of(sampleProduct));
        when(inventoryPersistencePort.findByProductId(productId)).thenReturn(Optional.empty());

        // Act
        var result = inventoryUseCase.getInventoryByProductId(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(0, result.get().inventory().getQuantity());
    }

    @Test
    void getInventoryByProductId_ReturnsEmptyWhenProductNotFound() {
        // Arrange
        when(productClientPort.getProductById(productId)).thenReturn(Optional.empty());

        // Act
        var result = inventoryUseCase.getInventoryByProductId(productId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void updateInventory_CreatesNewInventoryWhenNotFound() {
        // Arrange
        when(inventoryPersistencePort.findByProductId(productId)).thenReturn(Optional.empty());
        when(inventoryPersistencePort.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Inventory result = inventoryUseCase.updateInventory(productId, 15);

        // Assert
        assertEquals(15, result.getQuantity());
        verify(inventoryPersistencePort, times(1)).save(any(Inventory.class));
    }

    @Test
    void getAllInventoryWithProducts() {
        // Arrange
        when(productClientPort.getAllProducts()).thenReturn(java.util.List.of(sampleProduct));
        when(inventoryPersistencePort.findAll()).thenReturn(java.util.List.of(sampleInventory));

        // Act
        var result = inventoryUseCase.getAllInventoryWithProducts();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleInventory.getQuantity(), result.get(0).inventory().getQuantity());
    }

    @Test
    void getPurchaseHistory() {
        // Arrange
        Purchase purchase = new Purchase(UUID.randomUUID(), productId, "Test", 2, BigDecimal.TEN, java.time.LocalDateTime.now());
        when(purchasePersistencePort.findAll()).thenReturn(java.util.List.of(purchase));

        // Act
        var result = inventoryUseCase.getPurchaseHistory();

        // Assert
        assertEquals(1, result.size());
        assertEquals(purchase.getId(), result.get(0).getId());
    }
}
