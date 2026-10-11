package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoodsReceiptItemResponse {

    private Long id;

    private Long purchaseOrderItemId;

    private Long productId;

    private String productName;

    private Integer quantityReceived;
}