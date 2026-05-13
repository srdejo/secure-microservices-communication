package com.linktic.inventoryservice.domain.exception;

public class InvalidInventoryDataException extends RuntimeException {
    public InvalidInventoryDataException(String message) {
        super(message);
    }
}
