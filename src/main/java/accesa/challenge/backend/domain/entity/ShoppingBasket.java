package accesa.challenge.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "baskets")
public class ShoppingBasket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "basket_products",
            joinColumns = @JoinColumn(name = "basket_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "supermarket", referencedColumnName = "supermarket"),
                    @JoinColumn(name = "creation_date", referencedColumnName = "creation_date"),
                    @JoinColumn(name = "product_id", referencedColumnName = "product_id"),
            }
    )
    private List<Product> products = new ArrayList<>();
}
