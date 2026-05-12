package com.linktic.inventoryservice.infrastructure.adapter.in.rest;

import com.linktic.inventoryservice.application.usecase.InventoryUseCase;
import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.infrastructure.adapter.in.rest.api.InventoryApi;
import com.linktic.inventoryservice.infrastructure.adapter.in.rest.dto.*;
import com.linktic.inventoryservice.infrastructure.adapter.mapper.InventoryMapper;
import com.linktic.inventoryservice.domain.port.ProductClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InventoryController implements InventoryApi {

    private final InventoryUseCase inventoryUseCase;
    private final InventoryMapper inventoryMapper;
    private final ProductClientPort productClientPort;

    @Override
    public ResponseEntity<JsonApiResponseInventoryListDTO> getAllInventory() {
        List<Inventory> inventories = inventoryUseCase.getAllInventory();
        List<ProductInfo> productInfos = productClientPort.getAllProducts();
        return ResponseEntity.ok(inventoryMapper.toInventoryListResponse(inventories, productInfos));
    }

    @Override
    public ResponseEntity<JsonApiResponseInventoryDTO> getInventory(@PathVariable UUID productId) {
        return inventoryUseCase.getInventoryByProductId(productId)
                .map(inventory -> {
                    // Fetch product info for 'included'
                    ProductInfo productInfo = productClientPort.getProductById(productId).orElse(null);
                    return inventoryMapper.toInventoryResponse(inventory, productInfo);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<JsonApiResponsePurchaseListDTO> getPurchaseHistory() {
        List<Purchase> purchases = inventoryUseCase.getPurchaseHistory();
        return ResponseEntity.ok(inventoryMapper.toPurchaseListResponse(purchases));
    }

    @Override
    public ResponseEntity<JsonApiResponsePurchaseDTO> processPurchase(@RequestBody PurchaseRequestDTO request) {
        Purchase purchase = inventoryUseCase.processPurchase(request.getProductId(), request.getQuantity());
        return new ResponseEntity<>(inventoryMapper.toJsonResponse(purchase), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<InventoryDTO> updateInventoryQuantity(@PathVariable("productId") UUID productId, @RequestBody UpdateInventoryQuantityRequestDTO request) {
        Inventory updated = inventoryUseCase.updateInventory(productId, request.getQuantity());
        return ResponseEntity.ok(inventoryMapper.toDto(updated));
    }
}
