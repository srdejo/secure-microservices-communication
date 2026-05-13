package com.linktic.inventoryservice.infrastructure.adapter.mapper;

import com.linktic.inventoryservice.domain.model.Inventory;
import com.linktic.inventoryservice.domain.model.Purchase;
import com.linktic.inventoryservice.domain.model.ProductInfo;
import com.linktic.inventoryservice.infrastructure.adapter.in.rest.dto.*;
import com.linktic.inventoryservice.infrastructure.adapter.out.persistence.InventoryEntity;
import com.linktic.inventoryservice.infrastructure.adapter.out.persistence.PurchaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "initialQuantity", source = "quantity")
    Inventory toDomain(InventoryEntity entity);
    
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "id", ignore = true)
    InventoryEntity toEntity(Inventory inventory);

    Purchase toDomain(PurchaseEntity entity);
    PurchaseEntity toEntity(Purchase purchase);

    // Mapping to Generated DTOs
    InventoryDTO toDto(Inventory domain);

    @Mapping(target = "totalPrice", expression = "java(purchase.getTotalPrice().doubleValue())")
    @Mapping(target = "timestamp", expression = "java(purchase.getTimestamp().atOffset(java.time.ZoneOffset.UTC))")
    PurchaseResponseDTO toPurchaseResponse(Purchase purchase);

    default JsonApiResponsePurchaseDTO toJsonResponse(Purchase purchase) {
        JsonApiDataPurchaseDTO data = new JsonApiDataPurchaseDTO();
        data.setType("purchases");
        data.setId(purchase.getId().toString());
        data.setAttributes(toPurchaseResponse(purchase));
        
        JsonApiResponsePurchaseDTO response = new JsonApiResponsePurchaseDTO();
        response.setData(data);
        return response;
    }

    default JsonApiResponsePurchaseListDTO toPurchaseListResponse(List<Purchase> purchases) {
        List<JsonApiDataPurchaseDTO> dataList = purchases.stream()
                .map(p -> {
                    JsonApiDataPurchaseDTO data = new JsonApiDataPurchaseDTO();
                    data.setType("purchases");
                    data.setId(p.getId().toString());
                    data.setAttributes(toPurchaseResponse(p));
                    return data;
                })
                .collect(Collectors.toList());

        JsonApiResponsePurchaseListDTO response = new JsonApiResponsePurchaseListDTO();
        response.setData(dataList);
        return response;
    }

    default JsonApiResponseInventoryDTO toInventoryResponse(Inventory inventory, ProductInfo includedProduct) {
        JsonApiDataInventoryDTO data = new JsonApiDataInventoryDTO();
        data.setType("inventories");
        data.setId(inventory.getProductId().toString());
        data.setAttributes(toDto(inventory));
        
        JsonApiResponseInventoryDTO response = new JsonApiResponseInventoryDTO();
        response.setData(data);
        if (includedProduct != null) {
            response.setIncluded(java.util.Collections.singletonList(mapToProductResource(includedProduct)));
        }
        return response;
    }

    default JsonApiResponseInventoryListDTO toInventoryListResponse(List<Inventory> inventories, List<ProductInfo> includedProducts) {
        List<JsonApiDataInventoryDTO> dataList = inventories.stream()
                .map(i -> {
                    JsonApiDataInventoryDTO data = new JsonApiDataInventoryDTO();
                    data.setType("inventories");
                    data.setId(i.getProductId().toString());
                    data.setAttributes(toDto(i));
                    return data;
                })
                .collect(Collectors.toList());

        JsonApiResponseInventoryListDTO response = new JsonApiResponseInventoryListDTO();
        response.setData(dataList);
        if (includedProducts != null && !includedProducts.isEmpty()) {
            List<Object> included = includedProducts.stream()
                    .map(this::mapToProductResource)
                    .collect(Collectors.toList());
            response.setIncluded(included);
        }
        return response;
    }

    private Map<String, Object> mapToProductResource(ProductInfo product) {
        Map<String, Object> resource = new HashMap<>();
        resource.put("type", "products");
        resource.put("id", product.getId().toString());
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", product.getName());
        attributes.put("description", product.getDescription());
        attributes.put("price", product.getPrice());
        
        resource.put("attributes", attributes);
        return resource;
    }
}
