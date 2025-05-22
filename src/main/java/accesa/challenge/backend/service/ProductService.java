package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.dto.ProductBestDiscountDTO;
import accesa.challenge.backend.domain.entity.ProductDiscount;
import accesa.challenge.backend.repository.ProductDiscountRepository;
import accesa.challenge.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductDiscountRepository productDiscountRepository;

    /**
     * Retrieves a list of products with the highest available discount for
     * each unique product and brand combination.
     *
     * @return a list of ProductBestDiscountDTO objects,
     * each representing the best discount for a specific product.
     */
    public List<ProductBestDiscountDTO> getBestDiscounts() {
        return productDiscountRepository.findAll().stream()
                .filter(d -> d.getProduct() != null && d.getDiscountPercentage() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getProduct().getProductName() + "|" + d.getProduct().getBrand(),
                        Collectors.maxBy(Comparator.comparing(ProductDiscount::getDiscountPercentage))
                ))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(d -> new ProductBestDiscountDTO(
                        d.getProduct().getProductName(),
                        d.getProduct().getBrand(),
                        d.getProduct().getProductId().getSupermarket(),
                        d.getDiscountPercentage()
                ))
                .toList();
    }

}
