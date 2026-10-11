package com.inventory.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "goods_receipt_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_goods_receipt_po_item",
                        columnNames = {
                                "goods_receipt_id",
                                "purchase_order_item_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_goods_receipt_items_receipt",
                        columnList = "goods_receipt_id"
                ),
                @Index(
                        name = "idx_goods_receipt_items_po_item",
                        columnList = "purchase_order_item_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goods_receipt_id",
            nullable = false
    )
    private GoodsReceipt goodsReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_item_id",
            nullable = false
    )
    private PurchaseOrderItem purchaseOrderItem;

    @Column(
            name = "quantity_received",
            nullable = false
    )
    private Integer quantityReceived;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;
}