package com.inventory.procurement.service;

import com.inventory.procurement.dto.StockAdjustmentRequest;
import com.inventory.procurement.dto.StockMovementResponse;
import com.inventory.procurement.entity.Product;
import com.inventory.procurement.entity.StockMovement;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.enums.NotificationType;
import com.inventory.procurement.enums.StockMovementType;
import com.inventory.procurement.repository.ProductRepository;
import com.inventory.procurement.repository.StockMovementRepository;
import com.inventory.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository
            stockMovementRepository;

    private final ProductRepository
            productRepository;

    private final UserRepository
            userRepository;

    private final NotificationService
            notificationService;

    private final AuditLogService
            auditLogService;

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getAllMovements() {

        return stockMovementRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public StockMovementResponse adjustStock(
            StockAdjustmentRequest request,
            String userEmail
    ) {

        if (request.getMovementType()
                != StockMovementType.ADJUSTMENT_IN
                &&
                request.getMovementType()
                        != StockMovementType.ADJUSTMENT_OUT) {

            throw new RuntimeException(
                    "Only ADJUSTMENT_IN or ADJUSTMENT_OUT are allowed"
            );
        }

        Product product =
                productRepository
                        .findById(request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        int oldStock =
                product.getCurrentStock();

        int newStock;

        if (request.getMovementType()
                == StockMovementType.ADJUSTMENT_IN) {

            newStock =
                    oldStock
                            + request.getQuantity();

        } else {

            newStock =
                    oldStock
                            - request.getQuantity();

            if (newStock < 0) {

                throw new RuntimeException(
                        "Stock cannot become negative"
                );
            }
        }

        product.setCurrentStock(
                newStock
        );

        productRepository.save(product);

        StockMovement movement =
                new StockMovement();

        movement.setProduct(product);

        movement.setMovementType(
                request.getMovementType()
        );

        movement.setQuantity(
                request.getQuantity()
        );

        movement.setReason(
                request.getReason()
        );

        movement.setPerformedBy(user);

        movement.setReferenceType(
                "MANUAL_ADJUSTMENT"
        );

        StockMovement savedMovement =
                stockMovementRepository
                        .save(movement);

        auditLogService.log(
                user,
                "STOCK_ADJUSTMENT",
                "PRODUCT",
                product.getId(),
                String.valueOf(oldStock),
                String.valueOf(newStock)
        );

        if (newStock <
                product.getReorderLevel()) {

            notificationService.createNotification(
                    user,
                    NotificationType.LOW_STOCK,
                    "Low Stock Alert",
                    product.getName()
                            + " is below its reorder level.",
                    "PRODUCT",
                    product.getId()
            );
        }

        return mapToResponse(
                savedMovement
        );
    }

    private StockMovementResponse mapToResponse(
            StockMovement movement
    ) {

        return new StockMovementResponse(
                movement.getId(),

                movement.getProduct() != null
                        ? movement
                        .getProduct()
                        .getName()
                        : null,

                movement.getMovementType() != null
                        ? movement
                        .getMovementType()
                        .name()
                        : null,

                movement.getQuantity(),

                movement.getCreatedAt(),

                movement.getPerformedBy() != null
                        ? movement
                        .getPerformedBy()
                        .getEmail()
                        : null
        );
    }
}