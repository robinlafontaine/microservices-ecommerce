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

    public List<ProductData> findByProductName(String productName);

    public List<ProductData> findByCategoryId(int categoryId);

    public List<ProductData> findByPrice(BigDecimal price);

    public List<ProductData> findByPriceLessThan(BigDecimal price);

    public List<ProductData> findByPriceMoreThan(BigDecimal price);
}