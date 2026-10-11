package  com.inventory.procurement.repository;
import java.util.*;
import com.inventory.procurement.entity.StockMovement;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement,Long>{
    List<StockMovement> findByProductId(Long productId);
}