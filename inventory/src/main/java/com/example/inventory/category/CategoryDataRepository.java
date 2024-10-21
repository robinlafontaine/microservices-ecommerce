package com.example.inventory.category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Bastien Ubassy
 */
@Repository
public interface CategoryDataRepository extends JpaRepository<CategoryData, Long> {

    public List<CategoryData> findByCategoryName(String categoryName);
}