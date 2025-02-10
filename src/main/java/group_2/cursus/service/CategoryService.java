package group_2.cursus.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group_2.cursus.entity.Category;
import group_2.cursus.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getAllCategories(Pageable pageable) {
        return this.categoryRepository.findAll(pageable);
    }

    public Category getCategoryById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find category with id: " + id));
    }

    public Category createCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find category with id: " + id));
        category.setCategoryName(categoryDetails.getCategoryName());
        category.setStatus(categoryDetails.isStatus());
        category.setUpdatedAt(LocalDateTime.now());
        return this.categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find category with id: " + id));
        this.categoryRepository.delete(category);
    }

    public boolean categoryExists(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).isPresent();
    }
}
