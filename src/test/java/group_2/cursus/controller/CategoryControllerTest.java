package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Category;
import group_2.cursus.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());
        when(categoryService.getAllCategories(pageable)).thenReturn(page);

        ResponseEntity<APIResponse<Page<Category>>> response = categoryController.getAllCategories(1, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().getContent().size());
    }

    @Test
    void testGetCategoryById() {
        Long categoryId = 1L;
        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<APIResponse<Category>> response = categoryController.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody().getData());
    }

    @Test
    void testCreateCategory() {
        Category category = new Category();
        category.setCategoryName("Test Category");

        when(categoryService.categoryExists(category.getCategoryName())).thenReturn(false);
        when(categoryService.createCategory(category)).thenReturn(category);

        ResponseEntity<APIResponse<Category>> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Category created successfully", response.getBody().getMessage());
        assertEquals(category, response.getBody().getData());
    }

    @Test
    void testCreateCategoryNameExists() {
        Category category = new Category();
        category.setCategoryName("Existing Category");

        when(categoryService.categoryExists(category.getCategoryName())).thenReturn(true);

        ResponseEntity<APIResponse<Category>> response = categoryController.createCategory(category);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Category name already exists", response.getBody().getMessage());
    }

    @Test
    void testUpdateCategory() {
        Long categoryId = 1L;
        Category categoryDetails = new Category();
        Category updatedCategory = new Category();

        when(categoryService.updateCategory(categoryId, categoryDetails)).thenReturn(updatedCategory);

        ResponseEntity<APIResponse<Category>> response = categoryController.updateCategory(categoryId, categoryDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category updated successfully", response.getBody().getMessage());
        assertEquals(updatedCategory, response.getBody().getData());
    }

    @Test
    void testDeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryService).deleteCategory(categoryId);

        ResponseEntity<APIResponse<Void>> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Category deleted successfully", response.getBody().getMessage());
    }
}
