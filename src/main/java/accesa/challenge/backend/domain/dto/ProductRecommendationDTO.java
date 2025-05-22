package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductRecommendationDTO {
    private String productId;
    private String productName;
    private String brand;
    private String store;
    private LocalDate date;
    private double packageQuantity;
    private String packageUnit;
    private double price;
    private double valuePerUnit;
}
