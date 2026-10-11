package com.inventory.procurement.controller;

import com.inventory.procurement.dto.PurchaseOrderRequest;
import com.inventory.procurement.dto.PurchaseOrderResponse;
import com.inventory.procurement.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse>
    createOrder(
            @Valid
            @RequestBody
            PurchaseOrderRequest request,

            Authentication authentication
    ) {

        return new ResponseEntity<>(
                purchaseOrderService.createPurchaseOrder(
                        request,
                        authentication.getName()
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>>
    getAllOrders() {

        return ResponseEntity.ok(
                purchaseOrderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse>
    getOrderById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                purchaseOrderService.getOrderById(id)
        );
    }

    @PatchMapping("/{id}/order")
    public ResponseEntity<PurchaseOrderResponse>
    placeOrder(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                purchaseOrderService.placeOrder(id)
        );
    }
}