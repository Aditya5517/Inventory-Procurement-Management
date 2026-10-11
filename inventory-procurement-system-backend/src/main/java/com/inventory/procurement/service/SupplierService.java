package com.inventory.procurement.service;

import com.inventory.procurement.dto.SupplierRequest;
import com.inventory.procurement.dto.SupplierResponse;
import com.inventory.procurement.entity.Supplier;
import com.inventory.procurement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {

        Supplier supplier = new Supplier();

        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setIsActive(true);

        Supplier savedSupplier = supplierRepository.save(supplier);

        return mapToResponse(savedSupplier);
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found with id: " + id)
                );

        return mapToResponse(supplier);
    }

    @Transactional
    public SupplierResponse toggleSupplierStatus(Long id, boolean activeStatus) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found with id: " + id)
                );

        supplier.setIsActive(activeStatus);

        Supplier updatedSupplier = supplierRepository.save(supplier);

        return mapToResponse(updatedSupplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {

        return new SupplierResponse(
                supplier.getId(),
                supplier.getCompanyName(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getIsActive()
        );
    }
}