package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.dto.*;
import accesa.challenge.backend.domain.entity.*;
import accesa.challenge.backend.repository.BasketRepository;
import accesa.challenge.backend.repository.PriceAlertRepository;
import accesa.challenge.backend.repository.ProductRepository;
import accesa.challenge.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PriceAlertRepository priceAlertRepository;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new price alert for a user and product.
     *
     * @param request the alert creation request
     * @return the created PriceAlertDTO
     * @throws IllegalArgumentException if user or product not found, or target price invalid
     */
    public PriceAlertDTO createPriceAlert(AlertRequestDTO request) {
        User user = userRepository.findById(Integer.valueOf(request.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (request.getTargetPrice() <= 0) {
            throw new IllegalArgumentException("Target price must be greater than 0.");
        }

        if (!productRepository.existsProductByProductName(request.getProductName())) {
            throw new IllegalArgumentException("Product not found!");
        }

        PriceAlert alert = new PriceAlert();
        alert.setUser(user);
        alert.setProductName(request.getProductName());
        alert.setTargetPrice(request.getTargetPrice());

        PriceAlert savedAlert = priceAlertRepository.save(alert);

        return PriceAlertDTO.builder()
                .id(savedAlert.getId())
                .userEmail(savedAlert.getUser().getEmail())
                .productName(savedAlert.getProductName())
                .targetPrice(savedAlert.getTargetPrice())
                .alertTriggered(savedAlert.isAlertTriggered())
                .build();
    }

    /**
     * Adds a specified number of random, non-duplicate products to the user's shopping basket.
     * If the basket does not exist, it is created. Only products not already in the basket are added.
     *
     * @param userId                the ID of the user whose basket is to be updated
     * @param numberOfProductsToAdd the number of products to add to the basket
     * @return the updated ShoppingBasket entity
     * @throws IllegalArgumentException if the user is not found
     * @throws IllegalStateException    if there are no products available or no new products to add
     */
    @Transactional
    public ShoppingBasket addProductsToUserBasket(Integer userId, int numberOfProductsToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        ShoppingBasket basket = basketRepository.findByUser(user)
                .orElseGet(() -> basketRepository.save(ShoppingBasket.builder().user(user).products(new ArrayList<>()).build()));

        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            throw new IllegalStateException("No products available in the database.");
        }

        // Initialize basket products if null
        if (basket.getProducts() == null) {
            basket.setProducts(new ArrayList<>());
        }

        // Filter out products already in the basket
        Set<String> existingProductIds = basket.getProducts().stream()
                .map(p -> p.getProductId().getProductId())
                .collect(Collectors.toSet());

        List<Product> availableProducts = allProducts.stream()
                .filter(p -> !existingProductIds.contains(p.getProductId().getProductId()))
                .collect(Collectors.toList());

        if (availableProducts.isEmpty()) {
            throw new IllegalStateException("No new products available to add to the basket");
        }

        Collections.shuffle(availableProducts);

        List<Product> productsToAdd = availableProducts.stream()
                .limit(numberOfProductsToAdd)
                .toList();

        basket.getProducts().addAll(productsToAdd);

        return basketRepository.save(basket);
    }

    /**
     * Groups the user's basket products by supermarket and returns a list of ShoppingBasketDTOs,
     * each containing products from a single supermarket and the total cost.
     *
     * @param userId the ID of the user whose basket is to be optimized
     * @return a list of ShoppingBasketDTOs grouped by supermarket
     * @throws IllegalArgumentException if the user is not found
     * @throws IllegalStateException    if the basket is not found
     */
    @Transactional
    public List<ShoppingBasketDTO> optimizeUserBasket(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        ShoppingBasket basket = basketRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Basket not found!"));

        List<Product> products = Objects.requireNonNullElse(basket.getProducts(), Collections.emptyList());
        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        return products.stream()
                .collect(Collectors.groupingBy(p -> p.getProductId().getSupermarket()))
                .entrySet().stream()
                .map(entry -> {
                    String supermarket = entry.getKey();
                    List<ProductDTO> productDTOs = entry.getValue().stream()
                            .map(this::mapProductToDTO)
                            .toList();
                    double totalCost = entry.getValue().stream()
                            .mapToDouble(Product::getPrice)
                            .sum();
                    return ShoppingBasketDTO.builder()
                            .supermarket(supermarket)
                            .products(productDTOs)
                            .totalCost(totalCost)
                            .build();
                })
                .toList();
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param userDto the user details to register
     * @return the registered User entity
     * @throws IllegalArgumentException if the email is already taken
     */
    @Transactional
    public User registerUser(UserDTO userDto) {
        // Check if user exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already taken!");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .build();

        return userRepository.save(user);
    }

    private ProductDTO mapProductToDTO(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDTO.builder()
                .productId(product.getProductId().getProductId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .supermarket(product.getProductId().getSupermarket())
                .priceDate(product.getProductId().getCreationDate())
                .price(product.getPrice())
                .build();
    }
}
