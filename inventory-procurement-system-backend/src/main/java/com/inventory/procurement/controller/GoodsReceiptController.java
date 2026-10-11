package com.inventory.procurement.controller;

import com.inventory.procurement.dto.GoodsReceiptRequest;
import com.inventory.procurement.dto.GoodsReceiptResponse;
import com.inventory.procurement.service.GoodsReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods-receipts")
@RequiredArgsConstructor
public class GoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    @PostMapping
    public ResponseEntity<GoodsReceiptResponse>
    receiveGoods(
            @Valid @RequestBody
            GoodsReceiptRequest request,

            Authentication authentication
    ) {

        return new ResponseEntity<>(
                goodsReceiptService.receiveGoods(
                        request,
                        authentication.getName()
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<GoodsReceiptResponse>>
    getAllReceipts() {

        return ResponseEntity.ok(
                goodsReceiptService.getAllReceipts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptResponse>
    getReceiptById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                goodsReceiptService
                        .getReceiptById(id)
        );
    }
}