package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.dto.ProductBestDiscountDTO;
import accesa.challenge.backend.domain.dto.ProductNewDiscountDTO;
import accesa.challenge.backend.domain.entity.ProductDiscount;
import accesa.challenge.backend.repository.ProductDiscountRepository;
import accesa.challenge.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    /**
     * Retrieves a list of discount entries where the discount was created on the same day or
     * the day before the associated product's creation date
     * (which corresponds to the date the product prices were fetched, as derived from the filename).
     * The discount's createdAt field is automatically generated using a random date
     * between five days before and the day of the discount's start date (`discountFromDate`),
     * simulating when the discount was added.
     *
     * @return a list of ProductDiscount objects that meet the criteria.
     */
    public List<ProductNewDiscountDTO> getRecentDiscounts() {
        return productDiscountRepository.findAll().stream()
                .filter(discount -> {
                    if (discount == null || discount.getProduct() == null || discount.getProduct().getProductId() == null) {
                        return false;
                    }
                    LocalDate productCreatedDate = discount.getProduct().getProductId().getCreationDate();
                    LocalDate discountCreatedDate = discount.getCreatedAt();
                    if (productCreatedDate == null || discountCreatedDate == null) {
                        return false;
                    }
                    LocalDate dayBeforeProductCreated = productCreatedDate.minusDays(1);
                    return !discountCreatedDate.isBefore(dayBeforeProductCreated) &&
                            !discountCreatedDate.isAfter(productCreatedDate);
                })
                .map(d -> new ProductNewDiscountDTO(
                        d.getProduct().getProductName(),
                        d.getProduct().getBrand(),
                        d.getProduct().getProductId().getSupermarket(),
                        d.getDiscountPercentage(),
                        d.getCreatedAt(),
                        d.getProduct().getProductId().getCreationDate()
                ))
                .toList();
    }
}
