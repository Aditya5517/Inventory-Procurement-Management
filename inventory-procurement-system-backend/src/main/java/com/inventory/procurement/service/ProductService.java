package com.inventory.procurement.service;

import com.inventory.procurement.dto.ProductRequest;
import com.inventory.procurement.dto.ProductResponse;
import com.inventory.procurement.entity.Product;
import com.inventory.procurement.entity.ProductCategory;
import com.inventory.procurement.entity.Supplier;
import com.inventory.procurement.repository.ProductCategoryRepository;
import com.inventory.procurement.repository.ProductRepository;
import com.inventory.procurement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException(
                    "Product already exists with SKU: " + request.getSku()
            );
        }

        ProductCategory category =
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + request.getCategoryId()
                                )
                        );

        Supplier supplier =
                supplierRepository.findById(request.getSupplierId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Supplier not found with id: "
                                                + request.getSupplierId()
                                )
                        );

        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw new RuntimeException("Selected category is inactive");
        }

        if (!Boolean.TRUE.equals(supplier.getIsActive())) {
            throw new RuntimeException("Selected supplier is inactive");
        }

        Product product = new Product();

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setUnitCost(request.getUnitCost());
        product.setReorderLevel(request.getReorderLevel());
        product.setCurrentStock(request.getCurrentStock());
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        return mapToResponse(product);
    }

    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),

                product.getCategory() != null
                        ? product.getCategory().getId()
                        : null,

                product.getCategory() != null
                        ? product.getCategory().getName()
                        : null,

                product.getSupplier() != null
                        ? product.getSupplier().getId()
                        : null,

                product.getSupplier() != null
                        ? product.getSupplier().getCompanyName()
                        : null,

                product.getUnitCost(),
                product.getReorderLevel(),
                product.getCurrentStock(),
                product.getIsActive()
        );
    }
}