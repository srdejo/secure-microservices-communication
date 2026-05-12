package com.linktic.productservice.domain.port;

import com.linktic.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPersistencePort {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
}
