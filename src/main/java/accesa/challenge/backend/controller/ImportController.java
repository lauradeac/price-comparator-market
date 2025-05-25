package accesa.challenge.backend.controller;

import accesa.challenge.backend.domain.exception.CustomException;
import accesa.challenge.backend.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private final ImportService importService;

    @GetMapping("/products")
    public ResponseEntity<String> importAllProducts() {
        try {
            List<String> importedFiles = importService.readAllCsvFiles();
            String message = "Imported products from files: " + String.join(", ", importedFiles);
            return ResponseEntity.ok(message);
        } catch (CustomException ce) {
            return ResponseEntity.status(500).body("Import failed: " + ce.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error during import: " + e.getMessage());
        }
    }

    @GetMapping("/discounts")
    public ResponseEntity<String> importAllDiscounts() {
        try {
            List<String> importedFiles = importService.readAllDiscountCsvFiles();
            String message = "Imported discounts from files: " + String.join(", ", importedFiles);
            return ResponseEntity.ok(message);
        } catch (CustomException ce) {
            return ResponseEntity.status(500).body("Import failed: " + ce.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error during import: " + e.getMessage());
        }
    }
}
