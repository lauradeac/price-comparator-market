package accesa.challenge.backend.repository;

import accesa.challenge.backend.domain.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Integer> {
    List<PriceAlert> findByAlertTriggeredFalse();
}
