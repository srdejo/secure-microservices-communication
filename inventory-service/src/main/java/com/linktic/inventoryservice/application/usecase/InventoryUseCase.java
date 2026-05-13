package com.linktic.inventoryservice.application.usecase;

import com.linktic.inventoryservice.domain.event.InventoryChangedEvent;
import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.domain.model.InventoryWithProduct;
import java.util.stream.Collectors;
import com.linktic.inventoryservice.domain.port.InventoryPersistencePort;
import com.linktic.inventoryservice.domain.port.PurchasePersistencePort;
import com.linktic.inventoryservice.domain.port.ProductClientPort;
import com.linktic.inventoryservice.domain.port.InventoryServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryUseCase implements InventoryServicePort {

    private final InventoryPersistencePort inventoryPersistencePort;
    private final PurchasePersistencePort purchasePersistencePort;
    private final ProductClientPort productClientPort;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional(readOnly = true)
    public List<InventoryWithProduct> getAllInventoryWithProducts() {
        log.info("Fetching all inventory items with product info");
        List<Inventory> inventories = inventoryPersistencePort.findAll();
        List<ProductInfo> products = productClientPort.getAllProducts();
        
        return inventories.stream()
                .map(inventory -> {
                    ProductInfo info = products.stream()
                            .filter(p -> p.getId().equals(inventory.getProductId()))
                            .findFirst()
                            .orElse(null);
                    return new InventoryWithProduct(inventory, info);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchaseHistory() {
        log.info("Fetching purchase history");
        return purchasePersistencePort.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<InventoryWithProduct> getInventoryByProductId(UUID productId) {
        log.info("Fetching inventory for product ID: {}", productId);
        
        // 1. Fetch product info first
        Optional<ProductInfo> productInfo = productClientPort.getProductById(productId);
        
        if (productInfo.isEmpty()) {
            // Check if we have local inventory even if product service is down or product not found there
            // But usually, if product doesn't exist, inventory shouldn't either (or at least shouldn't be returned as valid)
            return Optional.empty();
        }

        // 2. Fetch inventory (or return virtual 0)
        Inventory inventory = inventoryPersistencePort.findByProductId(productId)
                .orElse(new Inventory(productId, 0));
                
        return Optional.of(new InventoryWithProduct(inventory, productInfo.get()));
    }

    @Transactional
    public Inventory updateInventory(UUID productId, Integer quantity) {
        log.info("Updating inventory for product ID: {} to quantity: {}", productId, quantity);
        Inventory inventory = inventoryPersistencePort.findByProductId(productId)
                .orElse(new Inventory(productId, 0));
        
        inventory.updateStock(quantity);
        Inventory saved = inventoryPersistencePort.save(inventory);
        
        log.debug("Inventory updated successfully for product ID: {}", productId);
        eventPublisher.publishEvent(new InventoryChangedEvent(saved.getProductId(), saved.getQuantity()));
        
        return saved;
    }

    @Transactional
    public Purchase processPurchase(UUID productId, Integer quantity) {
        log.info("Processing purchase for product ID: {}, quantity: {}", productId, quantity);
        // 1. Verify product exists in external service
        ProductInfo product = productClientPort.getProductById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found during purchase: {}", productId);
                    return new RuntimeException("Product not found: " + productId);
                });

        // 2. Update inventory
        Inventory inventory = inventoryPersistencePort.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("No inventory record for product: {}", productId);
                    return new RuntimeException("Insufficient inventory record");
                });

        log.debug("Found product: {} and inventory quantity: {}", product.getName(), inventory.getQuantity());
        
        inventory.removeStock(quantity);
        inventoryPersistencePort.save(inventory);

        // 3. Create purchase record
        BigDecimal total = product.getPrice().multiply(new BigDecimal(quantity));
        Purchase purchase = new Purchase(
                UUID.randomUUID(),
                productId,
                product.getName(),
                product.getDescription(),
                quantity,
                total,
                LocalDateTime.now()
        );

        log.info("Purchase successful: ID={}", purchase.getId());
        
        // Save the purchase record
        Purchase savedPurchase = purchasePersistencePort.save(purchase);
        
        // 4. Emit event
        eventPublisher.publishEvent(savedPurchase);

        return savedPurchase;
    }
}
