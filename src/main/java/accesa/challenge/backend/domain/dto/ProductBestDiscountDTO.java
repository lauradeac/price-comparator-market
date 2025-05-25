package accesa.challenge.backend.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductBestDiscountDTO {
    private String productName;
    private String brand;
    private String supermarket;
    private double discountPercentage;
}
