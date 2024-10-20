package com.example.inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Bastien Ubassy
 */
@Repository
public interface CategoryDataRepository extends JpaRepository<CategoryData, Long> {

    List<CategoryData> findByCategoryName(String categoryName);
}