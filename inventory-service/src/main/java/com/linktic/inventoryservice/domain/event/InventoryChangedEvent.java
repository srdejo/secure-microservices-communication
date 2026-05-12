package com.linktic.inventoryservice.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InventoryChangedEvent extends ApplicationEvent {
    private final Long productId;
    private final Integer newQuantity;

    public InventoryChangedEvent(Object source, Long productId, Integer newQuantity) {
        super(source);
        this.productId = productId;
        this.newQuantity = newQuantity;
    }
}
