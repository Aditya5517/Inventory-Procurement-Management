package com.inventory.procurement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsReceiptItemRequest {

    @NotNull(message = "Purchase Order Item ID is required")
    private Long purchaseOrderItemId;

    @NotNull(message = "Quantity received is required")
    @Positive(message = "Quantity received must be greater than zero")
    private Integer quantityReceived;
}