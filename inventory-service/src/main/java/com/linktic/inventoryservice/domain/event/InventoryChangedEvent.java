package com.linktic.inventoryservice.domain.event;

import java.util.UUID;

public class InventoryChangedEvent {
    private final UUID productId;
    private final Integer newQuantity;

    public InventoryChangedEvent(UUID productId, Integer newQuantity) {
        this.productId = productId;
        this.newQuantity = newQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }
}
