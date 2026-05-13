package com.linktic.productservice.domain.port;

import java.util.UUID;

public interface InventoryClientPort {
    void initializeInventory(UUID productId);
}
