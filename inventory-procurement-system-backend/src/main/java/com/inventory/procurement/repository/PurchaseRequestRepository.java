package com.inventory.procurement.repository;
import com.inventory.procurement.entity.PurchaseRequest;
import com.inventory.procurement.enums.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import  org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest,Long>{
    Optional<PurchaseRequest> findByRequestNumber(String requestNumber);
    List<PurchaseRequest> findByRequesterId(Long requesterId);
    List<PurchaseRequest> findByStatus(PurchaseRequestStatus status);
}
