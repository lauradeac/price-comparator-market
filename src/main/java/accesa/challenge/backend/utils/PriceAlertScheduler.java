package accesa.challenge.backend.utils;

import accesa.challenge.backend.domain.entity.PriceAlert;
import accesa.challenge.backend.domain.entity.Product;
import accesa.challenge.backend.domain.entity.ProductDiscount;
import accesa.challenge.backend.domain.entity.User;
import accesa.challenge.backend.repository.PriceAlertRepository;
import accesa.challenge.backend.repository.ProductDiscountRepository;
import accesa.challenge.backend.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PriceAlertScheduler {
    private final PriceAlertRepository alertRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;

    @Transactional
    @Scheduled(fixedRate = 60000) // every minute
    public void checkPriceAlerts() {
        List<PriceAlert> activeAlerts = alertRepository.findByAlertTriggeredFalse();

        for (PriceAlert alert : activeAlerts) {
            List<Product> matchingProducts = findMatchingProducts(alert);
            List<ProductDiscount> validDiscounts = findValidDiscounts(alert);

            if (!matchingProducts.isEmpty() || !validDiscounts.isEmpty()) {
                alert.setAlertTriggered(true);
                alertRepository.save(alert);
                sendNotification(alert.getUser(), matchingProducts, validDiscounts);
            }
        }
    }

    private List<Product> findMatchingProducts(PriceAlert alert) {
        return productRepository.findByProductName(alert.getProductName()).stream()
                .filter(p -> p.getPrice() <= alert.getTargetPrice())
                .toList();
    }

    private List<ProductDiscount> findValidDiscounts(PriceAlert alert) {
        return productDiscountRepository.findByProductProductName(alert.getProductName()).stream()
                .filter(d -> getDiscountedPrice(d) <= alert.getTargetPrice())
                .toList();
    }

    private double getDiscountedPrice(ProductDiscount discount) {
        double originalPrice = discount.getProduct().getPrice();
        return originalPrice * (1 - discount.getDiscountPercentage() / 100.0);
    }

    private void sendNotification(User user, List<Product> products, List<ProductDiscount> discounts) {
        System.out.printf("Notify %s:%n", user.getEmail());

        for (Product product : products) {
            System.out.printf(" - Product: %s | Store: %s | Price: %.2f | Date: %s%n",
                    product.getProductName(),
                    product.getProductId().getSupermarket(),
                    product.getPrice(),
                    product.getProductId().getCreationDate());
        }

        for (ProductDiscount discount : discounts) {
            System.out.printf(" - Product: %s | Store: %s | Discount Price: %.2f | Valid: %s to %s%n",
                    discount.getProduct().getProductName(),
                    discount.getProduct().getProductId().getSupermarket(),
                    getDiscountedPrice(discount),
                    discount.getDiscountFromDate(),
                    discount.getDiscountToDate());
        }
    }
}
