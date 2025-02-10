package group_2.cursus.controller;

import java.util.List;

import group_2.cursus.config.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import group_2.cursus.entity.Category;
import group_2.cursus.service.CategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/dashboard")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<APIResponse<Page<Category>>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Category> categories = categoryService.getAllCategories(pageable);
        APIResponse<Page<Category>> response = new APIResponse<>();
        response.setData(categories);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<APIResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        APIResponse<Category> response = new APIResponse<>();
        response.setData(category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/categories")
    public ResponseEntity<APIResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        APIResponse<Category> response = new APIResponse<>();
        if (categoryService.categoryExists(category.getCategoryName())) {
            response.setMessage("Category name already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        Category createdCategory = categoryService.createCategory(category);
        response.setData(createdCategory);
        response.setMessage("Category created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<APIResponse<Category>> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        APIResponse<Category> response = new APIResponse<>();
        response.setData(updatedCategory);
        response.setMessage("Category updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        APIResponse<Void> response = new APIResponse<>();
        response.setMessage("Category deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
