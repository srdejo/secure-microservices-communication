package com.linktic.productservice.domain.exception;

public class InvalidProductDataException extends RuntimeException {
    public InvalidProductDataException(String message) {
        super(message);
    }
}
