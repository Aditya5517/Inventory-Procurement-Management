package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponse {

    private Long id;

    private String poNumber;

    private Long supplierId;

    private String supplierName;

    private String status;

    private LocalDate expectedDeliveryDate;

    private BigDecimal totalCost;

    private String createdByEmail;

    private LocalDateTime orderedAt;

    private LocalDateTime createdAt;

    private List<PurchaseOrderItemResponse> items;
}