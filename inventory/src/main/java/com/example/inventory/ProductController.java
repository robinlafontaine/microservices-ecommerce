package com.example.inventory;

import com.example.inventory.orderItem.OrderItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bastien Ubassy
 */
@RestController
@RequestMapping("/inventory/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final MinioService minioService;

    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, MinioService minioService) {
        this.productService = productService;
        this.minioService = minioService;
    }

    // --- Create
    @PostMapping()
    public ResponseEntity<Void> createProduct(@RequestBody(required = false) ProductData productData) {
        if (productData == null) {
            return ResponseEntity.badRequest().body(null);
        }
        logger.info("Creating product: {}", productData.getProductName());
        productService.createProduct(productData);
        return ResponseEntity.noContent().build();
    }

    // --- Read
    @GetMapping("/search")
    public ResponseEntity<List<ProductData>> searchProducts(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Boolean underPrice
    ) {
        List<ProductData> products = productService.searchProducts(id, name, categoryId, price, underPrice);
        return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    }


    // --- Update
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id, @RequestBody(required = false) ProductData productData) {
        if (productData == null) {
            return ResponseEntity.badRequest().body(null);
        }
        logger.info("Updating product: {}", id);
        productService.updateProduct(id, productData);
        return ResponseEntity.noContent().build();
    }

    // --- Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product: {}", id);
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

    // --- Upload an image
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam(required = false) MultipartFile file) {
        if (file == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image file is required." + file));
        }
        logger.info("Uploading image: {}", file.getOriginalFilename());
        try {
            String url = minioService.uploadImage(file);
            logger.info("Image uploaded: {}", url);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            logger.error("Error uploading image", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error uploading image"));
        }
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@RequestBody List<OrderItemDTO> items) {
        return ResponseEntity.ok(productService.checkStock(items));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestBody List<OrderItemDTO> items) {
        productService.reserveStock(items);
        return ResponseEntity.noContent().build();
    }
}
