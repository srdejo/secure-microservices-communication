package com.linktic.productservice.infrastructure.adapter.in.rest;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.domain.port.ProductServicePort;
import com.linktic.productservice.infrastructure.adapter.in.rest.api.ProductApi;
import com.linktic.productservice.infrastructure.adapter.in.rest.dto.*;
import com.linktic.productservice.infrastructure.adapter.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductServicePort productServicePort;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<JsonApiResponseProductDTO> createProduct(@Valid @RequestBody ProductDTO productDto) {
        Product domainProduct = productMapper.toDomain(productDto);
        Product created = productServicePort.createProduct(domainProduct);
        return new ResponseEntity<>(productMapper.toJsonResponse(created), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<JsonApiResponseProductDTO> getProductById(@PathVariable UUID id) {
        return productServicePort.getProductById(id)
                .map(productMapper::toJsonResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<JsonApiResponseProductListDTO> getAllProducts() {
        List<Product> products = productServicePort.getAllProducts();
        return ResponseEntity.ok(productMapper.toJsonResponse(products));
    }
}
