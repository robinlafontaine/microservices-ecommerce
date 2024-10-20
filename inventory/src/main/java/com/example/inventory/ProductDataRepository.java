package com.example.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Bastien Ubassy
 */
@Repository
public interface ProductDataRepository extends JpaRepository<ProductData, Long> {

    // Read
    List<ProductData> findByProductName(String productName);

    List<ProductData> findByCategoryId(Long categoryId);

    List<ProductData> findByPrice(BigDecimal price);

    List<ProductData> findByPriceLessThan(BigDecimal price);
}