package accesa.challenge.backend.repository;

import accesa.challenge.backend.domain.entity.ProductDiscount;
import accesa.challenge.backend.domain.entity.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, ProductId> {
    boolean existsById(@NonNull ProductId productId);
    List<ProductDiscount> findByProductProductName(String productName);
}
