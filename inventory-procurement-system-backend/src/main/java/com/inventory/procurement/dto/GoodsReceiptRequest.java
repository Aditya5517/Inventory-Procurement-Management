package com.inventory.procurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoodsReceiptRequest {

    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;

    private String notes;

    @Valid
    @NotEmpty(message = "At least one received item is required")
    private List<GoodsReceiptItemRequest> items;
}