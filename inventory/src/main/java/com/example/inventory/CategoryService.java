package com.example.inventory;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 *
 * @author Bastien Ubassy
 */
@Service
public class CategoryService {

    private final CategoryDataRepository categoryDataRepository;

    @Autowired
    public CategoryService(CategoryDataRepository categoryDataRepository) {
        this.categoryDataRepository = categoryDataRepository;
    }

    // --- Create
    public void createCategory(CategoryData categoryData) {
        categoryDataRepository.save(categoryData);
    }

    // --- Read
    public List<CategoryData> searchCategories(Long id, String name) {
        // Check for ID
        if (id != null) {
            CategoryData category = categoryDataRepository.findById(id).orElse(null);
            return category != null ? List.of(category) : Collections.emptyList();
        }

        if (name != null) {
            List<CategoryData> categories = categoryDataRepository.findByCategoryName(name);
            return categories != null ? categories : Collections.emptyList();
        }

        return categoryDataRepository.findAll();
    }

    // --- Update
    public CategoryData updateCategory(Long id, CategoryData updatedCategoryData) {
        List<CategoryData> categories = searchCategories(id, null);

        if (categories.isEmpty()) {
            throw new EntityNotFoundException("Category not found for ID: " + id);
        }

        CategoryData existingCategory = categories.getFirst();
        existingCategory.setCategoryName(updatedCategoryData.getCategoryName());

        return categoryDataRepository.save(existingCategory);
    }


    // --- Delete
    public void deleteCategory(Long id) {
        categoryDataRepository.deleteById(id);
    }
}
