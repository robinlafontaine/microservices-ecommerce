package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Bastien Ubassy
 */
@RestController
@RequestMapping("/api/inventory/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // --- Create
    @PostMapping()
    public ResponseEntity<List<ProductData>> createProduct(@RequestBody ProductData productData) {
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
    public ResponseEntity<ProductData> updateProduct(@PathVariable Long id, @RequestBody ProductData newProduct) {
        ProductData updatedProduct = productService.updateProduct(id, newProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    // --- Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
