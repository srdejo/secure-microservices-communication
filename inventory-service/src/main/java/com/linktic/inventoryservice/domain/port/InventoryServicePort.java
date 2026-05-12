package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.InventoryWithProduct;
import com.linktic.inventoryservice.domain.model.Purchase;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryServicePort {
    List<InventoryWithProduct> getAllInventoryWithProducts();
    Optional<InventoryWithProduct> getInventoryByProductId(UUID productId);
    List<Purchase> getPurchaseHistory();
    Inventory updateInventory(UUID productId, Integer quantity);
    Purchase processPurchase(UUID productId, Integer quantity);
}
