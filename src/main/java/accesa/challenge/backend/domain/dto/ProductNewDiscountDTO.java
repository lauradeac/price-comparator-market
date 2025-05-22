package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductNewDiscountDTO {
    private String productName;
    private String brand;
    private String supermarket;
    private double discountPercentage;
    private LocalDate discountCreatedAt;
    private LocalDate productFetchDate;
}
