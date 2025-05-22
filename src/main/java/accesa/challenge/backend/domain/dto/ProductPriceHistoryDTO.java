package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductPriceHistoryDTO {
    private String productId;
    private String productName;
    private String brand;
    private String supermarket;
    @Builder.Default
    private List<ProductPricePointDTO> priceHistory = new ArrayList<>();
}
