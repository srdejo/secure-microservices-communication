package com.linktic.inventoryservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.linktic.inventoryservice.domain.exception.InvalidPurchaseDataException;

/**
 * Domain model for a Purchase.
 * Implements an Immutable pattern to ensure data integrity after a purchase is processed.
 */
public class Purchase {
    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final String productDescription;
    private final Integer quantity;
    private final BigDecimal totalPrice;
    private final LocalDateTime timestamp;

    public Purchase(UUID id, UUID productId, String productName, String productDescription, Integer quantity, BigDecimal totalPrice, LocalDateTime timestamp) {
        validate(productId, quantity, totalPrice);
        this.id = id != null ? id : UUID.randomUUID();
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    private void validate(UUID productId, Integer quantity, BigDecimal totalPrice) {
        if (productId == null) throw new InvalidPurchaseDataException("Product ID cannot be null");
        if (quantity == null || quantity <= 0) throw new InvalidPurchaseDataException("Quantity must be greater than zero");
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPurchaseDataException("Total price cannot be negative");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
