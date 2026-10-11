package com.inventory.procurement.service;

import com.inventory.procurement.dto.GoodsReceiptItemRequest;
import com.inventory.procurement.dto.GoodsReceiptItemResponse;
import com.inventory.procurement.dto.GoodsReceiptRequest;
import com.inventory.procurement.dto.GoodsReceiptResponse;
import com.inventory.procurement.entity.GoodsReceipt;
import com.inventory.procurement.entity.GoodsReceiptItem;
import com.inventory.procurement.entity.Product;
import com.inventory.procurement.entity.PurchaseOrder;
import com.inventory.procurement.entity.PurchaseOrderItem;
import com.inventory.procurement.entity.StockMovement;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.enums.NotificationType;
import com.inventory.procurement.enums.PurchaseOrderStatus;
import com.inventory.procurement.enums.StockMovementType;
import com.inventory.procurement.repository.GoodsReceiptItemRepository;
import com.inventory.procurement.repository.GoodsReceiptRepository;
import com.inventory.procurement.repository.ProductRepository;
import com.inventory.procurement.repository.PurchaseOrderItemRepository;
import com.inventory.procurement.repository.PurchaseOrderRepository;
import com.inventory.procurement.repository.StockMovementRepository;
import com.inventory.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodsReceiptService {

    private final GoodsReceiptRepository
            goodsReceiptRepository;

    private final GoodsReceiptItemRepository
            goodsReceiptItemRepository;

    private final PurchaseOrderRepository
            purchaseOrderRepository;

    private final PurchaseOrderItemRepository
            purchaseOrderItemRepository;

    private final ProductRepository
            productRepository;

    private final UserRepository
            userRepository;

    private final StockMovementRepository
            stockMovementRepository;

    private final NotificationService
            notificationService;

    private final AuditLogService
            auditLogService;

    @Transactional
    public GoodsReceiptResponse receiveGoods(
            GoodsReceiptRequest request,
            String userEmail
    ) {

        /*
         * 1. Find Purchase Order.
         */
        PurchaseOrder purchaseOrder =
                purchaseOrderRepository
                        .findById(
                                request.getPurchaseOrderId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Order not found with id: "
                                                + request
                                                .getPurchaseOrderId()
                                )
                        );

        /*
         * Goods can only arrive after the PO
         * has actually been ordered.
         */
        if (purchaseOrder.getStatus()
                != PurchaseOrderStatus.ORDERED
                &&
                purchaseOrder.getStatus()
                        != PurchaseOrderStatus
                        .PARTIALLY_RECEIVED) {

            throw new RuntimeException(
                    "Goods can only be received for ORDERED or PARTIALLY_RECEIVED purchase orders"
            );
        }

        /*
         * 2. Find user performing receiving.
         */
        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + userEmail
                                )
                        );

        /*
         * Prevent duplicate PO item IDs inside
         * the same incoming request.
         */
        Set<Long> requestedItemIds =
                new HashSet<>();

        for (GoodsReceiptItemRequest itemRequest
                : request.getItems()) {

            if (!requestedItemIds.add(
                    itemRequest
                            .getPurchaseOrderItemId()
            )) {

                throw new RuntimeException(
                        "Duplicate Purchase Order Item ID: "
                                + itemRequest
                                .getPurchaseOrderItemId()
                );
            }
        }

        /*
         * 3. Validate every item before saving
         * anything.
         */
        for (GoodsReceiptItemRequest itemRequest
                : request.getItems()) {

            validateReceiptItem(
                    purchaseOrder,
                    itemRequest
            );
        }

        /*
         * 4. Create Goods Receipt.
         */
        GoodsReceipt receipt =
                new GoodsReceipt();

        receipt.setReceiptNumber(
                generateReceiptNumber()
        );

        receipt.setPurchaseOrder(
                purchaseOrder
        );

        receipt.setReceivedBy(
                user
        );

        receipt.setReceivedAt(
                LocalDateTime.now()
        );

        receipt.setNotes(
                request.getNotes()
        );

        GoodsReceipt savedReceipt =
                goodsReceiptRepository
                        .save(receipt);

        List<GoodsReceiptItem> savedItems =
                new ArrayList<>();

        /*
         * 5. Process every received item.
         */
        for (GoodsReceiptItemRequest itemRequest
                : request.getItems()) {

            PurchaseOrderItem purchaseOrderItem =
                    purchaseOrderItemRepository
                            .findById(
                                    itemRequest
                                            .getPurchaseOrderItemId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Purchase Order Item not found with id: "
                                                    + itemRequest
                                                    .getPurchaseOrderItemId()
                                    )
                            );

            /*
             * Save receipt item.
             */
            GoodsReceiptItem receiptItem =
                    new GoodsReceiptItem();

            receiptItem.setGoodsReceipt(
                    savedReceipt
            );

            receiptItem.setPurchaseOrderItem(
                    purchaseOrderItem
            );

            receiptItem.setQuantityReceived(
                    itemRequest
                            .getQuantityReceived()
            );

            GoodsReceiptItem savedItem =
                    goodsReceiptItemRepository
                            .save(receiptItem);

            savedItems.add(
                    savedItem
            );

            /*
             * 6. Increase product stock.
             */
            Product product =
                    purchaseOrderItem
                            .getProduct();

            int oldStock =
                    product.getCurrentStock();

            int newStock =
                    oldStock
                            + itemRequest
                            .getQuantityReceived();

            product.setCurrentStock(
                    newStock
            );

            productRepository.save(
                    product
            );

            /*
             * 7. Create StockMovement.
             */
            StockMovement stockMovement =
                    new StockMovement();

            stockMovement.setProduct(
                    product
            );

            stockMovement.setMovementType(
                    StockMovementType.STOCK_IN
            );

            stockMovement.setQuantity(
                    itemRequest
                            .getQuantityReceived()
            );

            stockMovement.setReason(
                    "Goods received against Purchase Order "
                            + purchaseOrder
                            .getPoNumber()
            );

            stockMovement.setPerformedBy(
                    user
            );

            stockMovement.setReferenceType(
                    "GOODS_RECEIPT"
            );

            stockMovement.setReferenceId(
                    savedReceipt.getId()
            );

            stockMovementRepository.save(
                    stockMovement
            );

            /*
             * Audit stock change.
             */
            auditLogService.log(
                    user,
                    "STOCK_IN",
                    "PRODUCT",
                    product.getId(),
                    String.valueOf(oldStock),
                    String.valueOf(newStock)
            );
        }

        /*
         * Save existing PO status before changing it.
         */
        PurchaseOrderStatus oldStatus =
                purchaseOrder.getStatus();

        /*
         * 8. Calculate PO status.
         */
        updatePurchaseOrderStatus(
                purchaseOrder
        );

        PurchaseOrder updatedOrder =
                purchaseOrderRepository
                        .save(purchaseOrder);

        PurchaseOrderStatus newStatus =
                updatedOrder.getStatus();

        /*
         * 9. Notify if PO status changed.
         */
        if (oldStatus != newStatus) {

            notificationService
                    .createNotification(
                            updatedOrder
                                    .getCreatedBy(),

                            NotificationType
                                    .PO_STATUS_CHANGED,

                            "Purchase Order Status Changed",

                            "Purchase Order "
                                    + updatedOrder
                                    .getPoNumber()
                                    + " is now "
                                    + newStatus.name()
                                    + ".",

                            "PURCHASE_ORDER",

                            updatedOrder.getId()
                    );

            auditLogService.log(
                    user,
                    "UPDATE_STATUS",
                    "PURCHASE_ORDER",
                    updatedOrder.getId(),
                    oldStatus.name(),
                    newStatus.name()
            );
        }

        /*
         * 10. Audit the receipt itself.
         */
        auditLogService.log(
                user,
                "RECEIVE_GOODS",
                "GOODS_RECEIPT",
                savedReceipt.getId(),
                null,
                "Purchase Order: "
                        + purchaseOrder
                        .getPoNumber()
        );

        return mapToResponse(
                savedReceipt,
                savedItems
        );
    }

    /*
     * Ensures:
     *
     * 1. PO Item exists.
     * 2. PO Item belongs to selected PO.
     * 3. Received quantity does not exceed ordered.
     */
    private void validateReceiptItem(
            PurchaseOrder purchaseOrder,
            GoodsReceiptItemRequest request
    ) {

        PurchaseOrderItem purchaseOrderItem =
                purchaseOrderItemRepository
                        .findById(
                                request
                                        .getPurchaseOrderItemId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Order Item not found with id: "
                                                + request
                                                .getPurchaseOrderItemId()
                                )
                        );

        if (!purchaseOrderItem
                .getPurchaseOrder()
                .getId()
                .equals(
                        purchaseOrder.getId()
                )) {

            throw new RuntimeException(
                    "Purchase Order Item does not belong to Purchase Order "
                            + purchaseOrder
                            .getId()
            );
        }

        Long alreadyReceived =
                goodsReceiptItemRepository
                        .getTotalReceivedQuantity(
                                purchaseOrderItem
                                        .getId()
                        );

        if (alreadyReceived == null) {

            alreadyReceived = 0L;
        }

        long newTotal =
                alreadyReceived
                        + request
                        .getQuantityReceived();

        if (newTotal >
                purchaseOrderItem
                        .getQuantityOrdered()) {

            throw new RuntimeException(
                    "Received quantity exceeds ordered quantity for product: "
                            + purchaseOrderItem
                            .getProduct()
                            .getName()
            );
        }
    }

    /*
     * Determines whether PO is:
     *
     * PARTIALLY_RECEIVED
     *
     * or
     *
     * RECEIVED
     */
    private void updatePurchaseOrderStatus(
            PurchaseOrder purchaseOrder
    ) {

        List<PurchaseOrderItem> poItems =
                purchaseOrderItemRepository
                        .findByPurchaseOrderId(
                                purchaseOrder
                                        .getId()
                        );

        if (poItems.isEmpty()) {

            throw new RuntimeException(
                    "Purchase Order contains no items"
            );
        }

        boolean allItemsReceived =
                true;

        for (PurchaseOrderItem item
                : poItems) {

            Long totalReceived =
                    goodsReceiptItemRepository
                            .getTotalReceivedQuantity(
                                    item.getId()
                            );

            if (totalReceived == null) {

                totalReceived = 0L;
            }

            if (totalReceived <
                    item.getQuantityOrdered()) {

                allItemsReceived = false;
                break;
            }
        }

        if (allItemsReceived) {

            purchaseOrder.setStatus(
                    PurchaseOrderStatus.RECEIVED
            );

        } else {

            purchaseOrder.setStatus(
                    PurchaseOrderStatus
                            .PARTIALLY_RECEIVED
            );
        }
    }

    @Transactional(readOnly = true)
    public List<GoodsReceiptResponse>
    getAllReceipts() {

        return goodsReceiptRepository
                .findAll()
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public GoodsReceiptResponse
    getReceiptById(
            Long id
    ) {

        GoodsReceipt receipt =
                goodsReceiptRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Goods Receipt not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(
                receipt
        );
    }

    private GoodsReceiptResponse
    mapToResponse(
            GoodsReceipt receipt
    ) {

        List<GoodsReceiptItem> items =
                goodsReceiptItemRepository
                        .findByGoodsReceiptId(
                                receipt.getId()
                        );

        return mapToResponse(
                receipt,
                items
        );
    }

    private GoodsReceiptResponse
    mapToResponse(
            GoodsReceipt receipt,
            List<GoodsReceiptItem> items
    ) {

        List<GoodsReceiptItemResponse>
                itemResponses =
                items.stream()
                        .map(
                                this::mapItemToResponse
                        )
                        .toList();

        return new GoodsReceiptResponse(

                receipt.getId(),

                receipt.getReceiptNumber(),

                receipt.getPurchaseOrder() != null
                        ? receipt
                        .getPurchaseOrder()
                        .getId()
                        : null,

                receipt.getPurchaseOrder() != null
                        ? receipt
                        .getPurchaseOrder()
                        .getPoNumber()
                        : null,

                receipt.getReceivedBy() != null
                        ? receipt
                        .getReceivedBy()
                        .getEmail()
                        : null,

                receipt.getReceivedAt(),

                receipt.getNotes(),

                itemResponses
        );
    }

    private GoodsReceiptItemResponse
    mapItemToResponse(
            GoodsReceiptItem item
    ) {

        Product product =
                item
                        .getPurchaseOrderItem()
                        .getProduct();

        return new GoodsReceiptItemResponse(

                item.getId(),

                item
                        .getPurchaseOrderItem()
                        .getId(),

                product != null
                        ? product.getId()
                        : null,

                product != null
                        ? product.getName()
                        : null,

                item.getQuantityReceived()
        );
    }

    private String generateReceiptNumber() {

        return "GR-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}