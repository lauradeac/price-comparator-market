package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.dto.ProductBestDiscountDTO;
import accesa.challenge.backend.domain.dto.ProductNewDiscountDTO;
import accesa.challenge.backend.domain.dto.ProductPriceHistoryDTO;
import accesa.challenge.backend.domain.dto.ProductPricePointDTO;
import accesa.challenge.backend.domain.entity.Product;
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

    /**
     * Retrieves the price history for products filtered by store, category, brand, and date range.
     * For each product (grouped by productId and supermarket), it builds a history of price points
     * for each day in the given range, including any applicable discounts.
     *
     * @param store     optional store name to filter products
     * @param category  optional category to filter products
     * @param brand     optional brand to filter products
     * @param fromDate  start date of the price history range (inclusive)
     * @param toDate    end date of the price history range (inclusive)
     * @return a list of ProductPriceHistoryDTO, each containing the price history for a product
     */
    public List<ProductPriceHistoryDTO> getPriceHistoryWithRange(
            String store, String category, String brand, LocalDate fromDate, LocalDate toDate
    ) {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return List.of();

        // Filter products
        if (store != null)
            products = products.stream().filter(p -> p.getProductId().getSupermarket().equals(store)).toList();
        if (category != null)
            products = products.stream().filter(p -> p.getProductCategory().equals(category)).toList();
        if (brand != null)
            products = products.stream().filter(p -> p.getBrand().equals(brand)).toList();

        List<ProductDiscount> discounts = productDiscountRepository.findAll();

        Map<String, List<ProductDiscount>> discountMap = discounts.stream()
                .collect(Collectors.groupingBy(this::getProductDiscountKey));

        Map<String, List<Product>> productMap = products.stream()
                .collect(Collectors.groupingBy(this::getProductKey));

        List<ProductPriceHistoryDTO> historyDTOList = new ArrayList<>();

        productMap.forEach((key, productEntries) -> {
            productEntries = productEntries.stream()
                    .sorted(Comparator.comparing(p -> p.getProductId().getCreationDate()))
                    .toList();

            Product firstProduct = productEntries.get(0);
            String[] keyParts = key.split("\\|");
            String productId = keyParts[0];
            String supermarket = keyParts[1];

            ProductPriceHistoryDTO historyDTO = ProductPriceHistoryDTO.builder()
                    .productId(productId)
                    .productName(firstProduct.getProductName())
                    .brand(firstProduct.getBrand())
                    .supermarket(supermarket)
                    .build();

            for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                Product latest = getLatestProductByDate(productEntries, date);
                if (latest != null) {
                    double originalPrice = latest.getPrice();
                    double finalPrice = originalPrice;

                    List<ProductDiscount> discountList = discountMap.getOrDefault(key, List.of());
                    LocalDate finalDate = date;
                    Optional<ProductDiscount> discountOpt = discountList.stream()
                            .filter(d -> !finalDate.isBefore(d.getDiscountFromDate()) && !finalDate.isAfter(d.getDiscountToDate()))
                            .findFirst();

                    int discountPercentage = discountOpt.map(d -> d.getDiscountPercentage().intValue()).orElse(0);
                    if (discountPercentage > 0) {
                        finalPrice = originalPrice * (1 - discountPercentage / 100.0);
                    }

                    historyDTO.getPriceHistory().add(
                            ProductPricePointDTO.builder()
                                    .date(date)
                                    .originalPrice(originalPrice)
                                    .discountPercentage(discountPercentage)
                                    .finalPrice(finalPrice)
                                    .build()
                    );
                }
            }
            historyDTO.getPriceHistory().sort(Comparator.comparing(ProductPricePointDTO::getDate));
            historyDTOList.add(historyDTO);
        });

        return historyDTOList;
    }

    private String getProductKey(Product product) {
        return product.getProductId().getProductId() + "|" + product.getProductId().getSupermarket();
    }

    private String getProductDiscountKey(ProductDiscount discount) {
        return discount.getProduct().getProductId().getProductId() + "|" + discount.getProduct().getProductId().getSupermarket();
    }

    private Product getLatestProductByDate(List<Product> products, LocalDate date) {
        return products.stream()
                .filter(p -> !p.getProductId().getCreationDate().isAfter(date))
                .reduce((first, second) -> second)
                .orElse(null);
    }

}
