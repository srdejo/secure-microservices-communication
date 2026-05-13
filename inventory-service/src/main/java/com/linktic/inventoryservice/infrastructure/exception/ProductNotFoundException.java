package com.linktic.inventoryservice.infrastructure.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }
}
