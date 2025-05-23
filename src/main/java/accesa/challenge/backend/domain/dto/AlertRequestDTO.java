package accesa.challenge.backend.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlertRequestDTO {
    private String userId;
    private String productName;
    private double targetPrice;
}
