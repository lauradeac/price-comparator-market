package accesa.challenge.backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represents the composite key for the Product entity.
 * It consists of the product ID, the creation date and the supermarket.
 */
@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductId implements Serializable {

    @Column(name = "product_id")
    private String productId;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "supermarket")
    private String supermarket;
}
