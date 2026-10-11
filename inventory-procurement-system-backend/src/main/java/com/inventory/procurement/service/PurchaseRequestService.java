package com.inventory.procurement.service;

import com.inventory.procurement.dto.PurchaseRequestDto;
import com.inventory.procurement.dto.PurchaseRequestResponse;
import com.inventory.procurement.entity.Product;
import com.inventory.procurement.entity.PurchaseRequest;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.enums.NotificationType;
import com.inventory.procurement.enums.PurchaseRequestStatus;
import com.inventory.procurement.repository.ProductRepository;
import com.inventory.procurement.repository.PurchaseRequestRepository;
import com.inventory.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final NotificationService notificationService;

    private final AuditLogService auditLogService;

    @Transactional
    public PurchaseRequestResponse createPurchaseRequest(
            PurchaseRequestDto request,
            String userEmail
    ) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userEmail
                        )
                );

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: "
                                        + request.getProductId()
                        )
                );

        if (!Boolean.TRUE.equals(product.getIsActive())) {

            throw new RuntimeException(
                    "Cannot create purchase request for inactive product"
            );
        }

        PurchaseRequest purchaseRequest =
                new PurchaseRequest();

        purchaseRequest.setRequestNumber(
                generateRequestNumber()
        );

        purchaseRequest.setProduct(product);

        purchaseRequest.setQuantity(
                request.getQuantity()
        );

        purchaseRequest.setBusinessReason(
                request.getBusinessReason()
        );

        purchaseRequest.setRequester(user);

        purchaseRequest.setStatus(
                PurchaseRequestStatus.PENDING
        );

        purchaseRequest.setNeededByDate(
                request.getNeededByDate()
        );

        PurchaseRequest savedRequest =
                purchaseRequestRepository
                        .save(purchaseRequest);

        auditLogService.log(
                user,
                "CREATE",
                "PURCHASE_REQUEST",
                savedRequest.getId(),
                null,
                PurchaseRequestStatus.PENDING.name()
        );

        return mapToResponse(savedRequest);
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse>
    getAllRequests() {

        return purchaseRequestRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public PurchaseRequestResponse updateStatus(
            Long id,
            String status,
            String reviewerEmail,
            String rejectionNote
    ) {

        PurchaseRequest purchaseRequest =
                purchaseRequestRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Purchase Request not found with id: "
                                                + id
                                )
                        );

        User reviewer =
                userRepository
                        .findByEmail(reviewerEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reviewer not found: "
                                                + reviewerEmail
                                )
                        );

        PurchaseRequestStatus newStatus;

        try {

            newStatus =
                    PurchaseRequestStatus.valueOf(
                            status.toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new RuntimeException(
                    "Invalid purchase request status: "
                            + status
            );
        }

        if (newStatus ==
                PurchaseRequestStatus.PENDING) {

            throw new RuntimeException(
                    "Reviewed request cannot be changed back to PENDING"
            );
        }

        /*
         * Store current status before changing it.
         * We use this later in AuditLog.
         */
        PurchaseRequestStatus oldStatus =
                purchaseRequest.getStatus();

        if (oldStatus !=
                PurchaseRequestStatus.PENDING) {

            throw new RuntimeException(
                    "Only PENDING purchase requests can be reviewed"
            );
        }

        if (newStatus ==
                PurchaseRequestStatus.REJECTED) {

            if (rejectionNote == null ||
                    rejectionNote.trim().isEmpty()) {

                throw new RuntimeException(
                        "Rejection note is required when rejecting a request"
                );
            }

            purchaseRequest.setRejectionNote(
                    rejectionNote
            );

        } else {

            purchaseRequest.setRejectionNote(
                    null
            );
        }

        purchaseRequest.setStatus(
                newStatus
        );

        purchaseRequest.setReviewedBy(
                reviewer
        );

        purchaseRequest.setReviewedAt(
                LocalDateTime.now()
        );

        PurchaseRequest updatedRequest =
                purchaseRequestRepository
                        .save(purchaseRequest);

        /*
         * Create notification for requester.
         */
        if (newStatus ==
                PurchaseRequestStatus.APPROVED) {

            notificationService
                    .createNotification(
                            purchaseRequest.getRequester(),
                            NotificationType.REQUEST_APPROVED,
                            "Purchase Request Approved",
                            "Your purchase request "
                                    + purchaseRequest
                                    .getRequestNumber()
                                    + " has been approved.",
                            "PURCHASE_REQUEST",
                            purchaseRequest.getId()
                    );

        } else if (
                newStatus ==
                        PurchaseRequestStatus.REJECTED) {

            notificationService
                    .createNotification(
                            purchaseRequest.getRequester(),
                            NotificationType.REQUEST_REJECTED,
                            "Purchase Request Rejected",
                            "Your purchase request "
                                    + purchaseRequest
                                    .getRequestNumber()
                                    + " has been rejected.",
                            "PURCHASE_REQUEST",
                            purchaseRequest.getId()
                    );
        }

        /*
         * Record status change in audit log.
         */
        auditLogService.log(
                reviewer,
                "UPDATE_STATUS",
                "PURCHASE_REQUEST",
                purchaseRequest.getId(),
                oldStatus.name(),
                newStatus.name()
        );

        return mapToResponse(
                updatedRequest
        );
    }

    private String generateRequestNumber() {

        return "PR-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private PurchaseRequestResponse
    mapToResponse(
            PurchaseRequest request
    ) {

        return new PurchaseRequestResponse(

                request.getId(),

                request.getRequestNumber(),

                request.getProduct() != null
                        ? request
                        .getProduct()
                        .getId()
                        : null,

                request.getProduct() != null
                        ? request
                        .getProduct()
                        .getName()
                        : null,

                request.getQuantity(),

                request.getBusinessReason(),

                request.getRequester() != null
                        ? request
                        .getRequester()
                        .getEmail()
                        : null,

                request.getStatus() != null
                        ? request
                        .getStatus()
                        .name()
                        : null,

                request.getNeededByDate(),

                request.getReviewedBy() != null
                        ? request
                        .getReviewedBy()
                        .getEmail()
                        : null,

                request.getReviewedAt(),

                request.getRejectionNote(),

                request.getCreatedAt()
        );
    }
}