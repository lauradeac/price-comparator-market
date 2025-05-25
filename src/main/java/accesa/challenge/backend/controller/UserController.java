package accesa.challenge.backend.controller;

import accesa.challenge.backend.domain.dto.AlertRequestDTO;
import accesa.challenge.backend.domain.dto.PriceAlertDTO;
import accesa.challenge.backend.domain.dto.ShoppingBasketDTO;
import accesa.challenge.backend.domain.dto.UserDTO;
import accesa.challenge.backend.domain.entity.ShoppingBasket;
import accesa.challenge.backend.domain.entity.User;
import accesa.challenge.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/add-products/{userId}")
    public ResponseEntity<ShoppingBasket> addProductsToBasket(@PathVariable Integer userId) {
        try {
            ShoppingBasket updatedBasket = userService.addProductsToUserBasket(userId, 10);
            return ResponseEntity.ok(updatedBasket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/optimize-basket/{userId}")
    public ResponseEntity<List<ShoppingBasketDTO>> optimizeUserBasket(@PathVariable Integer userId) {
        try {
            List<ShoppingBasketDTO> optimizedLists = userService.optimizeUserBasket(userId);
            return ResponseEntity.ok(optimizedLists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDto) {
        User createdUser = userService.addUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}

