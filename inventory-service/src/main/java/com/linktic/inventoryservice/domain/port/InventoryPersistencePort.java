package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryPersistencePort {
    Optional<Inventory> findByProductId(UUID productId);
    Inventory save(Inventory inventory);
    List<Inventory> findAll();
}
