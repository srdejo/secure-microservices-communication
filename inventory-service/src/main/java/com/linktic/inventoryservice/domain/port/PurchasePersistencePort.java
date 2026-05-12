package com.linktic.inventoryservice.domain.port;

import com.linktic.inventoryservice.domain.model.Purchase;
import java.util.List;

public interface PurchasePersistencePort {
    Purchase save(Purchase purchase);
    List<Purchase> findAll();
}
