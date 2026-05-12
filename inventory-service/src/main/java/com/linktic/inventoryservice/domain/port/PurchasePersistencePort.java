package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.Purchase;

public interface PurchasePersistencePort {
    Purchase save(Purchase purchase);
    java.util.List<Purchase> findAll();
}
