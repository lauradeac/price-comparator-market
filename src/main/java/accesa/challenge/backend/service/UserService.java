package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.dto.AlertRequestDTO;
import accesa.challenge.backend.domain.dto.PriceAlertDTO;
import accesa.challenge.backend.domain.entity.PriceAlert;
import accesa.challenge.backend.domain.entity.User;
import accesa.challenge.backend.repository.PriceAlertRepository;
import accesa.challenge.backend.repository.ProductRepository;
import accesa.challenge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PriceAlertRepository priceAlertRepository;

    private final ProductRepository productRepository;

    /**
     * Creates a new price alert for a user and product.
     *
     * @param request the alert creation request
     * @return the created PriceAlertDTO
     * @throws IllegalArgumentException if user or product not found, or target price invalid
     */
    public PriceAlertDTO createPriceAlert(AlertRequestDTO request) {
        User user = userRepository.findById(Integer.valueOf(request.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (request.getTargetPrice() <= 0) {
            throw new IllegalArgumentException("Target price must be greater than 0.");
        }

        if (!productRepository.existsProductByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product not found!");
        }

        PriceAlert alert = new PriceAlert();
        alert.setUser(user);
        alert.setProductName(request.getProductName());
        alert.setTargetPrice(request.getTargetPrice());

        PriceAlert savedAlert = priceAlertRepository.save(alert);

        return PriceAlertDTO.builder()
                .id(savedAlert.getId())
                .userEmail(savedAlert.getUser().getEmail())
                .productName(savedAlert.getProductName())
                .targetPrice(savedAlert.getTargetPrice())
                .alertTriggered(savedAlert.isAlertTriggered())
                .build();
    }

}
