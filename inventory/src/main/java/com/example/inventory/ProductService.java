package com.example.inventory;

import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        List<ProductData> products = productDataRepository.findAll();

        // If there is no parameters, return all the products
        if (id == null && name == null && categoryId == null && price == null && underPrice == null) {
            return products;
        }

        if (id != null) {
            ProductData product = productDataRepository.findById(id).orElse(null);
            return product != null ? List.of(product) : Collections.emptyList();
        }

        if (name != null) {
            products = products.stream()
                    .filter(product -> product.getProductName().equals(name))
                    .collect(Collectors.toList());
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(product -> product.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        if (price != null) {
            if (underPrice != null && underPrice) {
                products = products.stream()
                        .filter(product -> product.getPrice().compareTo(price) < 0) // Prix inférieur à "price"
                        .collect(Collectors.toList());
            } else {
                products = products.stream()
                        .filter(product -> product.getPrice().compareTo(price) == 0) // Prix égal à "price"
                        .collect(Collectors.toList());
            }
        }

        return products;
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
