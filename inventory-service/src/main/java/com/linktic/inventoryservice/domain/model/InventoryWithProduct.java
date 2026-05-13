package com.linktic.inventoryservice.domain.model;

/**
 * Application DTO that combines inventory and product information.
 * Used to avoid leaking orchestration logic to the infrastructure layer.
 */
public record InventoryWithProduct(Inventory inventory, ProductInfo productInfo) {
}
