package accesa.challenge.backend.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PriceAlertDTO {
    private Long id;
    private String userEmail;
    private String productName;
    private double targetPrice;
    private boolean alertTriggered;
}
