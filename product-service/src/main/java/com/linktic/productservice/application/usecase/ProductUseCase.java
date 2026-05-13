package com.linktic.productservice.application.usecase;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.domain.port.InventoryClientPort;
import com.linktic.productservice.domain.port.ProductPersistencePort;
import com.linktic.productservice.domain.port.ProductServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUseCase implements ProductServicePort {

    private final ProductPersistencePort productPersistencePort;
    private final InventoryClientPort inventoryClientPort;

    @Override
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        
        log.debug("Initializing inventory for product ID: {}", product.getId());
        
        // 2. Initialize inventory (will throw exception if fails)
        inventoryClientPort.initializeInventory(product.getId());
        
        log.debug("Inventory initialized. Saving product to database.");
        
        // 3. Save product
        return productPersistencePort.save(product);
    }

    @Override
    public Optional<Product> getProductById(UUID id) {
        return productPersistencePort.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productPersistencePort.findAll();
    }
}
