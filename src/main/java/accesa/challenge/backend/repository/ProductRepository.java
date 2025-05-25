package accesa.challenge.backend.repository;

import accesa.challenge.backend.domain.entity.Product;
import accesa.challenge.backend.domain.entity.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductId> {
    boolean existsById(@NonNull ProductId productId);
    boolean existsProductByProductName(String productName);
    List<Product> findByProductName(String productName);
}
