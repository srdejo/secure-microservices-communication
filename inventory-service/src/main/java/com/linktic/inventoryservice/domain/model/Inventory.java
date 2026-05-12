package com.linktic.inventoryservice.domain.model;

import java.util.UUID;
import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import com.linktic.inventoryservice.domain.exception.InvalidInventoryDataException;

/**
 * Domain model for Inventory.
 * Encapsulates logic for stock management, ensuring state changes follow
 * business rules.
 */
public class Inventory {
    private final UUID productId;
    private Integer quantity;

    public Inventory(UUID productId, Integer initialQuantity) {
        if (productId == null)
            throw new InvalidInventoryDataException("Product ID is required");
        if (initialQuantity == null || initialQuantity < 0) {
            throw new InvalidInventoryDataException("Initial quantity cannot be negative");
        }
        this.productId = productId;
        this.quantity = initialQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void addStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidInventoryDataException("Amount to add must be positive");
        }
        this.quantity += amount;
    }

    public void removeStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidInventoryDataException("Amount to remove must be positive");
        }
        if (this.quantity < amount) {
            throw new InsufficientStockException("Insufficient stock for product " + productId);
        }
        this.quantity -= amount;
    }

    public void updateStock(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new InvalidInventoryDataException("New quantity cannot be negative");
        }
        this.quantity = newQuantity;
    }
}
