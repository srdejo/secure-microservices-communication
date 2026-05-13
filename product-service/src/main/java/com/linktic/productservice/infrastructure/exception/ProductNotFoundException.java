package com.linktic.productservice.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public ProductNotFoundException(UUID id) {
        super("Product not found with ID: " + id);
        this.code = "PRODUCT_NOT_FOUND";
        this.status = HttpStatus.NOT_FOUND;
    }
}
