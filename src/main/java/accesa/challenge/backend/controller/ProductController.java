package accesa.challenge.backend.controller;

import accesa.challenge.backend.domain.dto.ProductBestDiscountDTO;
import accesa.challenge.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
