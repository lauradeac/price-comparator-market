package accesa.challenge.backend.repository;

import accesa.challenge.backend.domain.entity.ShoppingBasket;
import accesa.challenge.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<ShoppingBasket, Integer> {
    Optional<ShoppingBasket> findByUser(User user);
}
