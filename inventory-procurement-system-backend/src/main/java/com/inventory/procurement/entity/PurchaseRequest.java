package com.inventory.procurement.entity;
import com.inventory.procurement.enums.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(
        name = "purchase_requests",
        indexes = {
                @Index(name = "idx_pr_product", columnList = "product_id"),
                @Index(name = "idx_pr_status", columnList = "status"),
                @Index(name = "idx_pr_requester", columnList = "requester_id"),
                @Index(name = "idx_pr_needed_by", columnList = "needed_by_date")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PurchaseRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_number", nullable = false, unique = true)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "business_reason", nullable = false)
    private String businessReason;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false)
    private PurchaseRequestStatus status = PurchaseRequestStatus.PENDING;

    @Column(name = "needed_by_date")
    private LocalDate neededByDate;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_note")
    private String rejectionNote;

    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}