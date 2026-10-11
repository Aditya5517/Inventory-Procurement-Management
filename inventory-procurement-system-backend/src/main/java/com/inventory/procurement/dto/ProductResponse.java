package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;

    private String sku;

    private String name;

    private String description;

    private Long categoryId;

    private String categoryName;

    private Long supplierId;

    private String supplierName;

    private BigDecimal unitCost;

    private Integer reorderLevel;

    private Integer currentStock;

    private Boolean isActive;
}