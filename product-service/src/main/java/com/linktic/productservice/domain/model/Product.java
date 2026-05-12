package com.linktic.productservice.domain.model;

import java.math.BigDecimal;
import java.util.UUID;
import com.linktic.productservice.domain.exception.InvalidProductDataException;

/**
 * Domain model for Product.
 */
public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;

    public Product(UUID id, String name, String description, BigDecimal price) {
        validatePrice(price);
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    public void updateDetails(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductDataException("Product name cannot be empty");
        }
        this.name = name;
        this.description = description;
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException("Price cannot be negative");
        }
    }
}
