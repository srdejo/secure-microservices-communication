package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.ProductInfo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductClientPort {
    Optional<ProductInfo> getProductById(UUID id);
    List<ProductInfo> getAllProducts();
}
