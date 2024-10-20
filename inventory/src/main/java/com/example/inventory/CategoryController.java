package com.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 *
 * @author Bastien Ubassy
 */
@RestController
@RequestMapping("/api/inventory/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // --- Create
    @PostMapping()
    public ResponseEntity<List<CategoryData>> createCategory(@RequestBody CategoryData categoryData) {
        categoryService.createCategory(categoryData);
        return ResponseEntity.noContent().build();
    }

    // --- Read
    @GetMapping("/search")
    public ResponseEntity<List<CategoryData>> searchCategories(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name
    ) {
        List<CategoryData> categories = categoryService.searchCategories(id, name);
        return categories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categories);
    }


    // --- Update
    @PutMapping("/{id}")
    public ResponseEntity<CategoryData> updateCategory(@PathVariable Long id, @RequestBody CategoryData newCategory) {
        CategoryData updatedCategory = categoryService.updateCategory(id, newCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    // --- Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
