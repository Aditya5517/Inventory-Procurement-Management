package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class StockMovementResponse {
    private Long id;
    private String productName;
    private String movementType;
    private Integer quantity;
    private LocalDateTime timestamp; // keeps this field as 'timestamp' in the API output
    private String performedByEmail;
}