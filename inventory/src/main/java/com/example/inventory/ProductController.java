package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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

    @Autowired
    public ProductController(ProductService productService, MinioService minioService) {
        this.productService = productService;
        this.minioService = minioService;
    }

    // --- Create
    @PostMapping()
    public ResponseEntity createProduct(@RequestBody(required = false) ProductData productData) {
        if (productData == null) {
            return ResponseEntity.badRequest().body("Access granted to update this product, but request body is required.");
        }
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
    public ResponseEntity updateProduct(@PathVariable Long id, @RequestBody(required = false) ProductData newProduct) {
        if (newProduct == null) {
            return ResponseEntity.badRequest().body("Access granted to update this product, but request body is required.");
        }

        ProductData updatedProduct = productService.updateProduct(id, newProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    // --- Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Upload an image
    @PostMapping
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            return minioService.uploadImage(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
