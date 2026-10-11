package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoodsReceiptResponse {

    private Long id;

    private String receiptNumber;

    private Long purchaseOrderId;

    private String poNumber;

    private String receivedByEmail;

    private LocalDateTime receivedAt;

    private String notes;

    private List<GoodsReceiptItemResponse> items;
}