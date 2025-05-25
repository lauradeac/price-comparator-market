package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductPricePointDTO {
    private LocalDate date;
    private double originalPrice;
    private int discountPercentage;
    private double finalPrice;
}
