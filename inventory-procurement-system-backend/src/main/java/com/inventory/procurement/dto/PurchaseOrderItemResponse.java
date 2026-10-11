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
public class PurchaseOrderItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private Long purchaseRequestId;

    private Integer quantityOrdered;

    private BigDecimal unitCost;

    private BigDecimal lineTotal;
}