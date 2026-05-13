package com.linktic.productservice.domain.port;

import com.linktic.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductServicePort {
    Product createProduct(Product product);
    Optional<Product> getProductById(UUID id);
    List<Product> getAllProducts();
}
