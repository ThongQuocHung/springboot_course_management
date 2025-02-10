package group_2.cursus.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import group_2.cursus.entity.Category;
import group_2.cursus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group_2.cursus.entity.SubCategory;
import group_2.cursus.repository.SubCategoryRepository;

@Service
public class SubCategoryService {

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<SubCategory> getAllSubCategories(Pageable pageable) {
        return this.subCategoryRepository.findAll(pageable);
    }

    public SubCategory getSubCategoryById(Long id) {
        return this.subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find subcategory with id: " + id));
    }

    public SubCategory createSubCategory(SubCategory subCategory) {
        Optional<Category> categoryOptional = categoryRepository.findById(subCategory.getCategory().getCategoryId());
        if (categoryOptional.isPresent()) {
            subCategory.setCategory(categoryOptional.get());
        } else {
            throw new RuntimeException("Category not found");
        }
        return subCategoryRepository.save(subCategory);
    }

    public SubCategory updateSubCategory(Long id, SubCategory subCategoryDetails) {
        SubCategory subCategory = this.subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find subcategory with id: " + id));
        subCategory.setSubCategoryName(subCategoryDetails.getSubCategoryName());
        subCategory.setStatus(subCategoryDetails.isStatus());
        subCategory.setUpdatedAt(LocalDateTime.now());
        if (subCategoryDetails.getCategory() != null) {
            subCategory.setCategory(subCategoryDetails.getCategory());
        }
        return this.subCategoryRepository.save(subCategory);
    }

    public void deleteSubCategory(Long id) {
        SubCategory subCategory = this.subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find subcategory with id: " + id));
        this.subCategoryRepository.delete(subCategory);
    }



    public boolean subCategoryExists(String subCategoryName) {
        return this.subCategoryRepository.findBySubCategoryName(subCategoryName).isPresent();
    }
}
