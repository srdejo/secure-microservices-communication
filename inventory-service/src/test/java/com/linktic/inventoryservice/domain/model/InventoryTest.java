package com.linktic.inventoryservice.domain.model;

import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testInventoryCreation() {
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory(productId, 10);

        assertEquals(productId, inventory.getProductId());
        assertEquals(10, inventory.getQuantity());
    }



    @Test
    void testInventoryCreation_NegativeQuantity() {
        UUID productId = UUID.randomUUID();
        assertThrows(com.linktic.inventoryservice.domain.exception.InvalidInventoryDataException.class, () -> new Inventory(productId, -5));
    }

    @Test
    void testAddStock() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 10);
        inventory.addStock(5);
        assertEquals(15, inventory.getQuantity());
    }

    @Test
    void testAddStock_Negative() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 10);
        assertThrows(com.linktic.inventoryservice.domain.exception.InvalidInventoryDataException.class, () -> inventory.addStock(-5));
    }

    @Test
    void testRemoveStock() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 10);
        inventory.removeStock(5);
        assertEquals(5, inventory.getQuantity());
    }

    @Test
    void testRemoveStock_Negative() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 10);
        assertThrows(com.linktic.inventoryservice.domain.exception.InvalidInventoryDataException.class, () -> inventory.removeStock(-5));
    }

    @Test
    void testRemoveStock_InsufficientStock() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 10);
        assertThrows(InsufficientStockException.class, () -> inventory.removeStock(15));
    }
}
