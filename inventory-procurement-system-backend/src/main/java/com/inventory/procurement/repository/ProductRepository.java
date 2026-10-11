package com.inventory.procurement.repository;
import com.inventory.procurement.entity.Product;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

@Repository
 public interface ProductRepository extends JpaRepository<Product,Long>{
    Optional<Product> findBySku(String sku);
    Boolean existsBySku(String sku);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findBySupplier(Long supplierId);
 }