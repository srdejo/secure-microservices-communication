package com.linktic.productservice.infrastructure.exception;

import com.linktic.productservice.domain.exception.InvalidProductDataException;
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
        return buildErrorResponse(ex.getMessage(), ex.getCode(), ex.getStatus());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getCode(), ex.getStatus());
    }

    @ExceptionHandler(InvalidProductDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductData(InvalidProductDataException ex) {
        return buildErrorResponse(ex.getMessage(), "INVALID_PRODUCT_DATA", HttpStatus.BAD_REQUEST);
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
