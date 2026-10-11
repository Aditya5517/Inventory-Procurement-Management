package com.inventory.procurement.service;

import com.inventory.procurement.dto.PurchaseOrderItemRequest;
import com.inventory.procurement.dto.PurchaseOrderItemResponse;
import com.inventory.procurement.dto.PurchaseOrderRequest;
import com.inventory.procurement.dto.PurchaseOrderResponse;
import com.inventory.procurement.entity.Product;
import com.inventory.procurement.entity.PurchaseOrder;
import com.inventory.procurement.entity.PurchaseOrderItem;
import com.inventory.procurement.entity.PurchaseRequest;
import com.inventory.procurement.entity.Supplier;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.enums.NotificationType;
import com.inventory.procurement.enums.PurchaseOrderStatus;
import com.inventory.procurement.enums.PurchaseRequestStatus;
import com.inventory.procurement.repository.PurchaseOrderItemRepository;
import com.inventory.procurement.repository.PurchaseOrderRepository;
import com.inventory.procurement.repository.PurchaseRequestRepository;
import com.inventory.procurement.repository.SupplierRepository;
import com.inventory.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository
            purchaseOrderRepository;

    private final PurchaseOrderItemRepository
            purchaseOrderItemRepository;

    private final PurchaseRequestRepository
            purchaseRequestRepository;

    private final SupplierRepository
            supplierRepository;

    private final UserRepository
            userRepository;

    private final AuditLogService
            auditLogService;

    private final NotificationService
            notificationService;

    @Transactional
    public PurchaseOrderResponse
    createPurchaseOrder(
            PurchaseOrderRequest request,
            String userEmail
    ) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + userEmail
                                )
                        );

        Supplier supplier =
                supplierRepository
                        .findById(
                                request.getSupplierId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Supplier not found with id: "
                                                + request
                                                .getSupplierId()
                                )
                        );

        if (!Boolean.TRUE.equals(
                supplier.getIsActive()
        )) {

            throw new RuntimeException(
                    "Cannot create purchase order for inactive supplier"
            );
        }

        PurchaseOrder purchaseOrder =
                new PurchaseOrder();

        purchaseOrder.setPoNumber(
                generatePoNumber()
        );

        purchaseOrder.setSupplier(
                supplier
        );

        purchaseOrder.setStatus(
                PurchaseOrderStatus.DRAFT
        );

        purchaseOrder.setExpectedDeliveryDate(
                request.getExpectedDeliveryDate()
        );

        purchaseOrder.setCreatedBy(
                user
        );

        purchaseOrder.setTotalCost(
                BigDecimal.ZERO
        );

        PurchaseOrder savedPurchaseOrder =
                purchaseOrderRepository
                        .save(purchaseOrder);

        List<PurchaseOrderItem> savedItems =
                new ArrayList<>();

        BigDecimal totalCost =
                BigDecimal.ZERO;

        for (PurchaseOrderItemRequest itemRequest
                : request.getItems()) {

            PurchaseRequest purchaseRequest =
                    purchaseRequestRepository
                            .findById(
                                    itemRequest
                                            .getPurchaseRequestId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Purchase Request not found with id: "
                                                    + itemRequest
                                                    .getPurchaseRequestId()
                                    )
                            );

            if (purchaseRequest.getStatus()
                    != PurchaseRequestStatus.APPROVED) {

                throw new RuntimeException(
                        "Purchase Request "
                                + purchaseRequest
                                .getRequestNumber()
                                + " is not approved"
                );
            }

            if (purchaseOrderItemRepository
                    .existsByPurchaseRequestId(
                            purchaseRequest.getId()
                    )) {

                throw new RuntimeException(
                        "Purchase Request "
                                + purchaseRequest
                                .getRequestNumber()
                                + " is already included in a Purchase Order"
                );
            }

            Product product =
                    purchaseRequest.getProduct();

            if (product == null) {

                throw new RuntimeException(
                        "Purchase Request has no product"
                );
            }

            if (!Boolean.TRUE.equals(
                    product.getIsActive()
            )) {

                throw new RuntimeException(
                        "Product is inactive: "
                                + product.getName()
                );
            }

            /*
             * Every item inside this PO must belong
             * to the selected supplier.
             */
            if (product.getSupplier() == null ||
                    !product
                            .getSupplier()
                            .getId()
                            .equals(
                                    supplier.getId()
                            )) {

                throw new RuntimeException(
                        "Product "
                                + product.getName()
                                + " does not belong to selected supplier"
                );
            }

            BigDecimal unitCost =
                    product.getUnitCost();

            BigDecimal lineTotal =
                    unitCost.multiply(
                            BigDecimal.valueOf(
                                    purchaseRequest
                                            .getQuantity()
                            )
                    );

            PurchaseOrderItem item =
                    new PurchaseOrderItem();

            item.setPurchaseOrder(
                    savedPurchaseOrder
            );

            item.setProduct(
                    product
            );

            item.setPurchaseRequest(
                    purchaseRequest
            );

            item.setQuantityOrdered(
                    purchaseRequest
                            .getQuantity()
            );

            item.setUnitCost(
                    unitCost
            );

            item.setLineTotal(
                    lineTotal
            );

            PurchaseOrderItem savedItem =
                    purchaseOrderItemRepository
                            .save(item);

            savedItems.add(
                    savedItem
            );

            totalCost =
                    totalCost.add(
                            lineTotal
                    );
        }

        savedPurchaseOrder.setTotalCost(
                totalCost
        );

        savedPurchaseOrder =
                purchaseOrderRepository
                        .save(
                                savedPurchaseOrder
                        );

        /*
         * Record PO creation.
         */
        auditLogService.log(
                user,
                "CREATE",
                "PURCHASE_ORDER",
                savedPurchaseOrder.getId(),
                null,
                savedPurchaseOrder
                        .getStatus()
                        .name()
        );

        return mapToResponse(
                savedPurchaseOrder,
                savedItems
        );
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse>
    getAllOrders() {

        return purchaseOrderRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse
    getOrderById(
            Long id
    ) {

        PurchaseOrder purchaseOrder =
                purchaseOrderRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Order not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(
                purchaseOrder
        );
    }

    @Transactional
    public PurchaseOrderResponse
    placeOrder(
            Long id
    ) {

        PurchaseOrder purchaseOrder =
                purchaseOrderRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Order not found with id: "
                                                + id
                                )
                        );

        if (purchaseOrder.getStatus()
                != PurchaseOrderStatus.DRAFT) {

            throw new RuntimeException(
                    "Only DRAFT purchase orders can be ordered"
            );
        }

        PurchaseOrderStatus oldStatus =
                purchaseOrder.getStatus();

        purchaseOrder.setStatus(
                PurchaseOrderStatus.ORDERED
        );

        purchaseOrder.setOrderedAt(
                LocalDateTime.now()
        );

        PurchaseOrder updatedOrder =
                purchaseOrderRepository
                        .save(
                                purchaseOrder
                        );

        /*
         * Audit status change.
         */
        auditLogService.log(
                purchaseOrder.getCreatedBy(),
                "UPDATE_STATUS",
                "PURCHASE_ORDER",
                purchaseOrder.getId(),
                oldStatus.name(),
                PurchaseOrderStatus
                        .ORDERED
                        .name()
        );

        /*
         * Notify PO creator.
         */
        notificationService
                .createNotification(
                        purchaseOrder
                                .getCreatedBy(),

                        NotificationType
                                .PO_STATUS_CHANGED,

                        "Purchase Order Status Changed",

                        "Purchase Order "
                                + purchaseOrder
                                .getPoNumber()
                                + " is now ORDERED.",

                        "PURCHASE_ORDER",

                        purchaseOrder
                                .getId()
                );

        return mapToResponse(
                updatedOrder
        );
    }

    private PurchaseOrderResponse
    mapToResponse(
            PurchaseOrder purchaseOrder
    ) {

        List<PurchaseOrderItem> items =
                purchaseOrderItemRepository
                        .findByPurchaseOrderId(
                                purchaseOrder
                                        .getId()
                        );

        return mapToResponse(
                purchaseOrder,
                items
        );
    }

    private PurchaseOrderResponse
    mapToResponse(
            PurchaseOrder purchaseOrder,
            List<PurchaseOrderItem> items
    ) {

        List<PurchaseOrderItemResponse>
                itemResponses =
                items.stream()
                        .map(
                                this::mapItemToResponse
                        )
                        .toList();

        return new PurchaseOrderResponse(

                purchaseOrder.getId(),

                purchaseOrder
                        .getPoNumber(),

                purchaseOrder.getSupplier() != null
                        ? purchaseOrder
                        .getSupplier()
                        .getId()
                        : null,

                purchaseOrder.getSupplier() != null
                        ? purchaseOrder
                        .getSupplier()
                        .getCompanyName()
                        : null,

                purchaseOrder.getStatus() != null
                        ? purchaseOrder
                        .getStatus()
                        .name()
                        : null,

                purchaseOrder
                        .getExpectedDeliveryDate(),

                purchaseOrder
                        .getTotalCost(),

                purchaseOrder.getCreatedBy() != null
                        ? purchaseOrder
                        .getCreatedBy()
                        .getEmail()
                        : null,

                purchaseOrder
                        .getOrderedAt(),

                purchaseOrder
                        .getCreatedAt(),

                itemResponses
        );
    }

    private PurchaseOrderItemResponse
    mapItemToResponse(
            PurchaseOrderItem item
    ) {

        return new PurchaseOrderItemResponse(

                item.getId(),

                item.getProduct() != null
                        ? item.getProduct()
                        .getId()
                        : null,

                item.getProduct() != null
                        ? item.getProduct()
                        .getName()
                        : null,

                item.getPurchaseRequest() != null
                        ? item
                        .getPurchaseRequest()
                        .getId()
                        : null,

                item.getQuantityOrdered(),

                item.getUnitCost(),

                item.getLineTotal()
        );
    }

    private String generatePoNumber() {

        return "PO-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}