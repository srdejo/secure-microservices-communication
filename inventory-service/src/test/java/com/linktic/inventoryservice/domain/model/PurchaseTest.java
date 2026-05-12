package com.linktic.inventoryservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {

    @Test
    void testPurchaseCreation() {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Purchase purchase = new Purchase(id, productId, "Laptop", 5, java.math.BigDecimal.TEN, now);

        assertEquals(id, purchase.getId());
        assertEquals(productId, purchase.getProductId());
        assertEquals("Laptop", purchase.getProductName());
        assertEquals(5, purchase.getQuantity());
        assertEquals(java.math.BigDecimal.TEN, purchase.getTotalPrice());
        assertEquals(now, purchase.getTimestamp());
    }

    @Test
    void testPurchaseCreation_Defaults() {
        UUID productId = UUID.randomUUID();

        Purchase purchase = new Purchase(null, productId, "Laptop", 5, java.math.BigDecimal.TEN, null);

        assertNotNull(purchase.getId());
        assertEquals(productId, purchase.getProductId());
        assertEquals(5, purchase.getQuantity());
        assertNotNull(purchase.getTimestamp());
    }

    @Test
    void testPurchaseCreation_NegativeQuantity() {
        UUID productId = UUID.randomUUID();
        assertThrows(com.linktic.inventoryservice.domain.exception.InvalidPurchaseDataException.class, () -> new Purchase(null, productId, "Laptop", -5, java.math.BigDecimal.TEN, null));
    }

    @Test
    void testPurchaseCreation_ZeroQuantity() {
        UUID productId = UUID.randomUUID();
        assertThrows(com.linktic.inventoryservice.domain.exception.InvalidPurchaseDataException.class, () -> new Purchase(null, productId, "Laptop", 0, java.math.BigDecimal.TEN, null));
    }
}
