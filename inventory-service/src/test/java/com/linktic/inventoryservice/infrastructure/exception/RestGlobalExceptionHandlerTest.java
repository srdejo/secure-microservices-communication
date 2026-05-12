package com.linktic.inventoryservice.infrastructure.exception;

import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestGlobalExceptionHandlerTest {

    private final RestGlobalExceptionHandler handler = new RestGlobalExceptionHandler();

    @Test
    void handleInsufficientStock() {
        InsufficientStockException ex = new InsufficientStockException("Not enough stock");
        ResponseEntity<RestGlobalExceptionHandler.ErrorResponse> response = handler.handleInsufficientStock(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().get(0).getDetail().contains("Not enough stock"));
    }

    @Test
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<RestGlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalArgument(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrors().get(0).getDetail().contains("Invalid argument"));
    }

    @Test
    void handleExternalServiceException() {
        ExternalServiceException ex = new ExternalServiceException("External error", "EXT_ERR", HttpStatus.SERVICE_UNAVAILABLE);
        ResponseEntity<RestGlobalExceptionHandler.ErrorResponse> response = handler.handleExternalServiceException(ex);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().getErrors().get(0).getDetail().contains("External error"));
    }

    @Test
    void handleGeneralException() {
        Exception ex = new Exception("Unknown error");
        ResponseEntity<RestGlobalExceptionHandler.ErrorResponse> response = handler.handleGeneralException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
