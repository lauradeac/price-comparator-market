package accesa.challenge.backend.controller;

import accesa.challenge.backend.domain.dto.AlertRequestDTO;
import accesa.challenge.backend.domain.dto.PriceAlertDTO;
import accesa.challenge.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping("/set-alert")
    public ResponseEntity<PriceAlertDTO> createAlert(@RequestBody AlertRequestDTO request) {
        try {
            PriceAlertDTO message = userService.createPriceAlert(request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
