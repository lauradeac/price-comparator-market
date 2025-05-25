package accesa.challenge.backend.domain.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {
    private String productId;
    private String productName;
    private String brand;
    private String supermarket;
    private LocalDate priceDate;
    private double price;
}
