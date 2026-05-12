package com.linktic.inventoryservice.infrastructure.exception;

import com.linktic.inventoryservice.domain.exception.InsufficientStockException;
import com.linktic.inventoryservice.domain.exception.InvalidInventoryDataException;
import com.linktic.inventoryservice.domain.exception.InvalidPurchaseDataException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getCode(), ex.getStatus());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        return buildErrorResponse(ex.getMessage(), "INSUFFICIENT_STOCK", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInventoryDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInventoryData(InvalidInventoryDataException ex) {
        return buildErrorResponse(ex.getMessage(), "INVALID_INVENTORY_DATA", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPurchaseDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPurchaseData(InvalidPurchaseDataException ex) {
        return buildErrorResponse(ex.getMessage(), "INVALID_PURCHASE_DATA", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), "INVALID_ARGUMENT", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return buildErrorResponse(ex.getMessage(), "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String detail, String code, HttpStatus status) {
        ErrorResponse error = ErrorResponse.builder()
                .errors(Collections.singletonList(
                        ErrorResponse.ErrorDetails.builder()
                                .status(String.valueOf(status.value()))
                                .code(code)
                                .detail(detail)
                                .build()
                ))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.api+json");

        return new ResponseEntity<>(error, headers, status);
    }

    @Data
    @Builder
    public static class ErrorResponse {
        private List<ErrorDetails> errors;

        @Data
        @Builder
        public static class ErrorDetails {
            private String status;
            private String code;
            private String detail;
        }
    }
}
