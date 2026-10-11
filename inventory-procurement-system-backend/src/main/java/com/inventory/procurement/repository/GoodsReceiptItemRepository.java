package com.inventory.procurement.repository;

import com.inventory.procurement.entity.GoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReceiptItemRepository
        extends JpaRepository<GoodsReceiptItem, Long> {

    List<GoodsReceiptItem> findByGoodsReceiptId(Long goodsReceiptId);

    List<GoodsReceiptItem> findByPurchaseOrderItemId(
            Long purchaseOrderItemId
    );

    @Query("""
            SELECT COALESCE(SUM(g.quantityReceived), 0)
            FROM GoodsReceiptItem g
            WHERE g.purchaseOrderItem.id = :purchaseOrderItemId
            """)
    Long getTotalReceivedQuantity(
            @Param("purchaseOrderItemId")
            Long purchaseOrderItemId
    );
}