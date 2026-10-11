package com.inventory.procurement.controller;

import com.inventory.procurement.dto.PurchaseRequestDto;
import com.inventory.procurement.dto.PurchaseRequestResponse;
import com.inventory.procurement.service.PurchaseRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping
    public ResponseEntity<PurchaseRequestResponse> createRequest(
            @Valid @RequestBody PurchaseRequestDto request,
            Authentication authentication
    ) {

        return new ResponseEntity<>(
                purchaseRequestService.createPurchaseRequest(
                        request,
                        authentication.getName()
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseRequestResponse>>
    getAllRequests() {

        return ResponseEntity.ok(
                purchaseRequestService.getAllRequests()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseRequestResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionNote,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                purchaseRequestService.updateStatus(
                        id,
                        status,
                        authentication.getName(),
                        rejectionNote
                )
        );
    }
}