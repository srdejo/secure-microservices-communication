package com.linktic.productservice.domain.model;

import com.linktic.productservice.domain.exception.InvalidProductDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testValidProductCreation() {
        Product product = new Product(UUID.randomUUID(), "Laptop", "Gaming Laptop", new BigDecimal("1500.00"));
        assertNotNull(product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("Gaming Laptop", product.getDescription());
        assertEquals(new BigDecimal("1500.00"), product.getPrice());
    }

    @Test
    void testInvalidProductUpdate_NullName() {
        Product product = new Product(UUID.randomUUID(), "Laptop", "Desc", BigDecimal.TEN);
        assertThrows(InvalidProductDataException.class, () -> product.updateDetails(null, "Desc"));
    }

    @Test
    void testInvalidProductUpdate_EmptyName() {
        Product product = new Product(UUID.randomUUID(), "Laptop", "Desc", BigDecimal.TEN);
        assertThrows(InvalidProductDataException.class, () -> product.updateDetails("   ", "Desc"));
    }

    @Test
    void testInvalidProductCreation_NullPrice() {
        assertThrows(InvalidProductDataException.class, () -> new Product(UUID.randomUUID(), "Name", "Desc", null));
    }

    @Test
    void testInvalidProductCreation_NegativePrice() {
        assertThrows(InvalidProductDataException.class, () -> new Product(UUID.randomUUID(), "Name", "Desc", new BigDecimal("-1")));
    }

    @Test
    void testUpdatePrice() {
        Product product = new Product(UUID.randomUUID(), "Laptop", "Desc", new BigDecimal("1000.00"));
        product.updatePrice(new BigDecimal("1200.00"));
        assertEquals(new BigDecimal("1200.00"), product.getPrice());
    }

    @Test
    void testUpdatePrice_Invalid() {
        Product product = new Product(UUID.randomUUID(), "Laptop", "Desc", new BigDecimal("1000.00"));
        assertThrows(InvalidProductDataException.class, () -> product.updatePrice(null));
        assertThrows(InvalidProductDataException.class, () -> product.updatePrice(new BigDecimal("-10")));
    }

    @Test
    void testReconstructProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Phone", "Smartphone", new BigDecimal("800.00"));
        assertEquals(id, product.getId());
        assertEquals("Phone", product.getName());
        assertEquals("Smartphone", product.getDescription());
        assertEquals(new BigDecimal("800.00"), product.getPrice());
    }
}
