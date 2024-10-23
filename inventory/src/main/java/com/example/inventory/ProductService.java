package com.example.inventory;

import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

/**
 *
 * @author Bastien Ubassy
 */
@Service
public class ProductService {

    private final ProductDataRepository productDataRepository;

    @Autowired
    public ProductService(ProductDataRepository productDataRepository) {
        this.productDataRepository = productDataRepository;
    }

    // --- Create
    public void createProduct(ProductData productData) {
        productDataRepository.save(productData);
    }

    // --- Read
    public List<ProductData> searchProducts(Long id, String name, Long categoryId, BigDecimal price, Boolean underPrice) {
        // If there is no parameters, return all the products
        if (id == null && name == null && categoryId == null && price == null && underPrice == null) {
            return productDataRepository.findAll();
        }

        // Check for ID
        if (id != null) {
            ProductData product = productDataRepository.findById(id).orElse(null);
            return product != null ? List.of(product) : Collections.emptyList();
        }

        Set<ProductData> products = new HashSet<>();

        // Create a map of search criteria to their corresponding method references
        Map<String, Supplier<List<ProductData>>> criteriaMap = getCriteriaMap(name, categoryId, price, underPrice);

        // Collect results from all applicable criteria
        for (Supplier<List<ProductData>> supplier : criteriaMap.values()) {
            products.addAll(supplier.get());
        }

        return new ArrayList<>(products);
    }

    @NotNull
    private Map<String, Supplier<List<ProductData>>> getCriteriaMap(String name, Long categoryId, BigDecimal price,Boolean underPrice) {
        Map<String, Supplier<List<ProductData>>> criteriaMap = new HashMap<>();

        if (name != null) {
            criteriaMap.put("name", () -> productDataRepository.findByProductName(name));
        }

        if (categoryId != null) {
            criteriaMap.put("categoryId", () -> productDataRepository.findByCategoryId(categoryId));
        }

        if (price != null) {
            if (underPrice != null && underPrice) {
                criteriaMap.put("underPrice", () -> productDataRepository.findByPriceLessThan(price));
            } else {
                criteriaMap.put("exactPrice", () -> productDataRepository.findByPrice(price));
            }
        }
        return criteriaMap;
    }

    // --- Update
    public ProductData updateProduct(Long id, ProductData updatedProductData) {
        List<ProductData> products = searchProducts(id, null, null, null, null);

        if (products. isEmpty()) {
            throw new EntityNotFoundException("Product not found for ID: " + id);
        }

        ProductData existingProduct = products.getFirst();

        existingProduct.setProductName(updatedProductData.getProductName());
        existingProduct.setDescription(updatedProductData.getDescription());
        existingProduct.setImageUrl(updatedProductData.getImageUrl());
        existingProduct.setPrice(updatedProductData.getPrice());
        existingProduct.setCategoryId(updatedProductData.getCategoryId());
        existingProduct.setStockQuantity(updatedProductData.getStockQuantity());

        return productDataRepository.save(existingProduct);
    }

    // --- Delete
    public void deleteProduct(Long id) {
        productDataRepository.deleteById(id);
    }
}
