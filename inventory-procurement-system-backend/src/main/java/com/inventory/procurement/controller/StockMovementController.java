package com.inventory.procurement.controller;

import com.inventory.procurement.dto.StockAdjustmentRequest;
import com.inventory.procurement.dto.StockMovementResponse;
import com.inventory.procurement.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService
            stockMovementService;

    @GetMapping
    public ResponseEntity<List<StockMovementResponse>>
    getAllMovements() {

        return ResponseEntity.ok(
                stockMovementService
                        .getAllMovements()
        );
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockMovementResponse>
    adjustStock(
            @Valid
            @RequestBody
            StockAdjustmentRequest request,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                stockMovementService
                        .adjustStock(
                                request,
                                authentication.getName()
                        )
        );
    }
}