package com.inventory.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderItemRequest {

    @NotNull(message = "Purchase Request ID is required")
    private Long purchaseRequestId;
}