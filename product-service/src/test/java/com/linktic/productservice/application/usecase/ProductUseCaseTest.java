package com.linktic.productservice.application.usecase;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.domain.port.InventoryClientPort;
import com.linktic.productservice.domain.port.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Mock
    private InventoryClientPort inventoryClientPort;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Product sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = new Product(productId, "Test Product", "Description", new BigDecimal("100.00"));
    }

    @Test
    void createProduct_Success() {
        // Arrange
        when(productPersistencePort.save(any(Product.class))).thenReturn(sampleProduct);
        doNothing().when(inventoryClientPort).initializeInventory(productId);

        // Act
        Product result = productUseCase.createProduct(sampleProduct);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(inventoryClientPort, times(1)).initializeInventory(productId);
        verify(productPersistencePort, times(1)).save(sampleProduct);
    }

    @Test
    void createProduct_FailsWhenInventoryInitFails() {
        // Arrange
        doThrow(new RuntimeException("External service down"))
                .when(inventoryClientPort).initializeInventory(productId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productUseCase.createProduct(sampleProduct));
        verify(productPersistencePort, never()).save(any());
    }

    @Test
    void getProductById_Found() {
        // Arrange
        when(productPersistencePort.findById(productId)).thenReturn(Optional.of(sampleProduct));

        // Act
        Optional<Product> result = productUseCase.getProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        when(productPersistencePort.findById(any())).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productUseCase.getProductById(UUID.randomUUID());

        // Assert
        assertFalse(result.isPresent());
    }
}
