package com.linktic.productservice.application.usecase;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.domain.port.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ProductUseCaseTest {

    @Mock
    private ProductPersistencePort persistencePort;

    @InjectMocks
    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        Product product = new Product(null, "Test", "Desc", BigDecimal.TEN);
        when(persistencePort.save(product)).thenReturn(product);

        Product result = productUseCase.createProduct(product);

        assertEquals(product, result);
        verify(persistencePort, times(1)).save(product);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Test", "Desc", BigDecimal.TEN);
        when(persistencePort.findById(id)).thenReturn(Optional.of(product));

        Optional<Product> result = productUseCase.getProductById(id);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }
}
