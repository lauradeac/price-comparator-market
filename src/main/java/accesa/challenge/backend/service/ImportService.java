package accesa.challenge.backend.service;

import accesa.challenge.backend.domain.entity.Product;
import accesa.challenge.backend.domain.entity.ProductDiscount;
import accesa.challenge.backend.domain.entity.ProductId;
import accesa.challenge.backend.domain.exception.CustomException;
import accesa.challenge.backend.repository.ProductDiscountRepository;
import accesa.challenge.backend.repository.ProductRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static accesa.challenge.backend.utils.FileDataExtractor.*;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ProductRepository productRepository;

    private final ProductDiscountRepository productDiscountRepository;

    /**
     * Reads all CSV files containing product data from the specified directory.
     * The files are expected to be in the classpath under "sample-data-files".
     *
     * @return A list of filenames that were successfully imported.
     */
    public List<String> readAllCsvFiles() {
        return readCsvFiles("classpath:sample-data-files/*.csv", this::importProducts, true);
    }

    /**
     * Reads all CSV files containing discount data from the specified directory.
     * The files are expected to be in the classpath under "sample-data-files".
     *
     * @return A list of filenames that were successfully imported.
     */
    public List<String> readAllDiscountCsvFiles() {
        return readCsvFiles("classpath:sample-data-files/*discounts*.csv", this::importDiscounts, false);
    }


    /**
     * Imports product data from the given CSV data and filename.
     *
     * @param data     The CSV data as a list of string arrays.
     * @param filename The name of the CSV file.
     */
    public void importProducts(List<String[]> data, String filename) {
        String supermarket = extractSupermarket(filename);
        LocalDate creationDate = extractCreationDate(filename);

        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length < 8) {
                System.err.println("Row " + i + " in file " + filename + " has insufficient columns.");
                continue;
            }

            try {
                String productIdValue = row[0];
                ProductId productId = new ProductId(productIdValue, creationDate, supermarket);

                if (productRepository.existsById(productId)) {
                    continue; // Skip duplicate
                }

                Product product = mapRowToProduct(row, productId);
                productRepository.save(product);
            } catch (Exception ex) {
                // Skip invalid row
                throw new CustomException("Failed to import row " + i + " in file " + filename, ex);
            }
        }
    }

    /**
     * Imports products discount data from the given CSV data and filename.
     *
     * @param data     The CSV data as a list of string arrays.
     * @param filename The name of the CSV file.
     */
    private void importDiscounts(List<String[]> data, String filename) {
        String supermarket = extractSupermarket(filename);
        LocalDate creationDate = extractCreationDateDiscounts(filename);

        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row.length < 9) {
                System.err.println("Row " + i + " in file " + filename + " has insufficient columns.");
                continue;
            }
            try {
                ProductDiscount discount = mapRowToProductDiscount(row, supermarket, creationDate);
                if (discount != null) {
                    productDiscountRepository.save(discount);
                }
            } catch (Exception ex) {
                throw new CustomException("Failed to import discount row " + i + " in file " + filename, ex);
            }
        }
    }

    /**
     * Reads CSV files from the specified pattern and applies the given import function.
     *
     * @param pattern        The pattern to match CSV files.
     * @param importFunction The function to apply to the CSV data.
     * @return A list of filenames that were successfully imported.
     */
    private List<String> readCsvFiles(String pattern, BiConsumer<List<String[]>, String> importFunction, boolean excludeDiscounts) {
        List<String> importedFiles = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(pattern);

            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (excludeDiscounts && filename.contains("discounts")) {
                    continue; // Skip discount files
                }
                try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                    CSVReader csvReader = new CSVReaderBuilder(reader)
                            .withCSVParser(parser)
                            .withSkipLines(1)
                            .build();

                    List<String[]> data = csvReader.readAll();
                    importFunction.accept(data, filename);
                    importedFiles.add(filename);
                }
            }
        } catch (Exception e) {
            throw new CustomException("Failed to import CSV files with pattern: " + pattern, e);
        }
        return importedFiles;
    }


    /**
     * Parses a single row of product data from the CSV file.
     *
     * @param row       The row data as a string array.
     * @param productId The product ID to associate with this product.
     * @return A Product object populated with the data from the row.
     */
    private Product mapRowToProduct(String[] row, ProductId productId) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(row[1]);
        product.setProductCategory(row[2]);
        product.setBrand(row[3]);
        product.setPackageQuantity(Double.parseDouble(row[4]));
        product.setPackageUnit(row[5]);
        product.setPrice(Double.parseDouble(row[6]));
        product.setCurrency(row[7]);
        return product;
    }

    /**
     * Parses a single row of discount data from the CSV file.
     *
     * @param row          The row data as a string array.
     * @param supermarket  The supermarket name.
     * @param creationDate The date when the discount was created.
     * @return A ProductDiscount object populated with the data from the row.
     */
    private ProductDiscount mapRowToProductDiscount(String[] row, String supermarket, LocalDate creationDate) {
        String productIdValue = row[0];
        ProductId productId = new ProductId(productIdValue, creationDate, supermarket);

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            // Product not found; skip this discount
            return null;
        }

        ProductDiscount discount = new ProductDiscount();
        discount.setProduct(productOpt.get());
        discount.setDiscountPercentage(Double.parseDouble(row[8]));
        discount.setDiscountFromDate(LocalDate.parse(row[6]));
        discount.setDiscountToDate(LocalDate.parse(row[7]));
        return discount;
    }
}
