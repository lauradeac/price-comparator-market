package accesa.challenge.backend.controller;

import accesa.challenge.backend.domain.dto.ProductBestDiscountDTO;
import accesa.challenge.backend.domain.dto.ProductNewDiscountDTO;
import accesa.challenge.backend.domain.dto.ProductPriceHistoryDTO;
import accesa.challenge.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private final ProductService productService;

    @GetMapping("/best-discounts")
    public ResponseEntity<List<ProductBestDiscountDTO>> getBestDiscounts() {
        List<ProductBestDiscountDTO> bestDiscounts = productService.getBestDiscounts();
        return ResponseEntity.ok(bestDiscounts);
    }

    @GetMapping("/new-discounts")
        public ResponseEntity<List<ProductNewDiscountDTO>> getRecentDiscounts() {
        return ResponseEntity.ok(productService.getRecentDiscounts());
    }

    // GET /api/products/price-history?store=kaufland&startDate=2025-05-01&endDate=2025-05-20
    @GetMapping("/price-history")
    public ResponseEntity<List<ProductPriceHistoryDTO>> getPriceHistory(
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ProductPriceHistoryDTO> history = productService.getPriceHistoryWithRange(store, category, brand, startDate, endDate);
        return history.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(history);
    }

}
