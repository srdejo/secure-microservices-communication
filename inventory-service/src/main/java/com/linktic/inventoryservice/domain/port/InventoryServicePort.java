package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.Purchase;
import java.util.Optional;
import java.util.UUID;

public interface InventoryServicePort {
    Optional<Inventory> getInventoryByProductId(UUID productId);
    Inventory updateInventory(UUID productId, Integer quantity);
    Purchase processPurchase(UUID productId, Integer quantity);
}
