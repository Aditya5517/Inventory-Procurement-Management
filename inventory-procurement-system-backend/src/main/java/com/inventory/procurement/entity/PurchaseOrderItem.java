package com.inventory.procurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "purchase_order_items",
        indexes = {
                @Index(
                        name = "idx_po_items_purchase_order",
                        columnList = "purchase_order_id"
                ),
                @Index(
                        name = "idx_po_items_product",
                        columnList = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_id",
            nullable = false
    )
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @Column(
            name = "quantity_ordered",
            nullable = false
    )
    private Integer quantityOrdered;

    @Column(
            name = "unit_cost",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitCost;

    @Column(
            name = "line_total",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal lineTotal;
}