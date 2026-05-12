package com.linktic.inventoryservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {
    private UUID id;
    private String name;
    private BigDecimal price;
}
