package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestResponse {

    private Long id;

    private String requestNumber;

    private Long productId;

    private String productName;

    private Integer quantity;

    private String businessReason;

    private String requesterEmail;

    private String status;

    private LocalDate neededByDate;

    private String reviewedByEmail;

    private LocalDateTime reviewedAt;

    private String rejectionNote;

    private LocalDateTime createdAt;
}