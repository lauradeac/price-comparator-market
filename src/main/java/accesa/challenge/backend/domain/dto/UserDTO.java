package accesa.challenge.backend.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
