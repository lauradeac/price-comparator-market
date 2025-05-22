package accesa.challenge.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_discounts")
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id", unique = true, nullable = false)
    private Integer discountId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id"),
            @JoinColumn(name = "creation_date", referencedColumnName = "creation_date"),
            @JoinColumn(name = "supermarket", referencedColumnName = "supermarket")})
    private Product product;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Column(name = "from_date", nullable = false)
    private LocalDate discountFromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate discountToDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null
                && this.product != null
                && this.product.getProductId() != null
                && this.product.getProductId().getCreationDate() != null
                && this.discountFromDate != null) {

            LocalDate endDate = this.discountFromDate;
            LocalDate startDate = endDate.minusDays(5);

            long startEpochDay = startDate.toEpochDay();
            long endEpochDay = endDate.toEpochDay();

            SecureRandom random = new SecureRandom();
            long randomEpochDay = startEpochDay + random.nextInt((int) (endEpochDay - startEpochDay + 1));
            this.createdAt = LocalDate.ofEpochDay(randomEpochDay);
        }
    }
}
