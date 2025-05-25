package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShoppingBasketDTO {
    private String supermarket;
    private List<ProductDTO> products;
    private double totalCost;
}
