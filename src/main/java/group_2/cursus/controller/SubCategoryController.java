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

import group_2.cursus.entity.SubCategory;
import group_2.cursus.service.SubCategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/dashboard")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping("/subcategories")
    public ResponseEntity<APIResponse<Page<SubCategory>>> getAllSubCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<SubCategory> subCategories = subCategoryService.getAllSubCategories(pageable);
        APIResponse<Page<SubCategory>> response = new APIResponse<>();
        response.setData(subCategories);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/subcategories/{id}")
    public ResponseEntity<APIResponse<SubCategory>> getSubCategoryById(@PathVariable Long id) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        APIResponse<SubCategory> response = new APIResponse<>();
        response.setData(subCategory);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/subcategories")
    public ResponseEntity<APIResponse<SubCategory>> createSubCategory(@Valid @RequestBody SubCategory subCategory) {
        APIResponse<SubCategory> response = new APIResponse<>();

        if (subCategoryService.subCategoryExists(subCategory.getSubCategoryName())) {
            response.setMessage("SubCategory name already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        SubCategory createdSubCategory = subCategoryService.createSubCategory(subCategory);
        response.setData(createdSubCategory);
        response.setMessage("SubCategory created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/subcategories/{id}")
    public ResponseEntity<APIResponse<SubCategory>> updateSubCategory(@PathVariable Long id,
            @Valid @RequestBody SubCategory subCategoryDetails) {
        SubCategory updatedSubCategory = subCategoryService.updateSubCategory(id, subCategoryDetails);
        APIResponse<SubCategory> response = new APIResponse<>();
        response.setData(updatedSubCategory);
        response.setMessage("SubCategory updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<APIResponse<Void>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        APIResponse<Void> response = new APIResponse<>();
        response.setMessage("SubCategory deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
