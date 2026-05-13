package com.linktic.inventoryservice.infrastructure.adapter.in.event;

import com.linktic.inventoryservice.domain.event.InventoryChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryEventListener {

    @EventListener
    public void handleInventoryChanged(InventoryChangedEvent event) {
        log.info("EVENT: Inventory changed for product {}. New quantity: {}", 
                event.getProductId(), event.getNewQuantity());
    }
}
