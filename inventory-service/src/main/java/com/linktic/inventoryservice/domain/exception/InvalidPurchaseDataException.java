package com.linktic.inventoryservice.domain.exception;

public class InvalidPurchaseDataException extends RuntimeException {
    public InvalidPurchaseDataException(String message) {
        super(message);
    }
}
