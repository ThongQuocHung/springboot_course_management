package group_2.cursus.service;

import group_2.cursus.entity.Category;
import group_2.cursus.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());
        when(categoryRepository.findAll(pageable)).thenReturn(page);

        Page<Category> result = categoryService.getAllCategories(pageable);

        assertEquals(categories, result.getContent());
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCategoryById() {
        Long id = 1L;
        Category category = new Category();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(id);

        assertEquals(category, result);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testGetCategoryByIdNotFound() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(id);
        });

        assertEquals("Cannot find category with id: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testCreateCategory() {
        Category category = new Category();
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertEquals(category, result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategory() {
        Long id = 1L;
        Category existingCategory = new Category();
        Category categoryDetails = new Category();
        categoryDetails.setCategoryName("Updated Name");
        categoryDetails.setStatus(true);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.updateCategory(id, categoryDetails);

        assertEquals(existingCategory, result);
        assertEquals("Updated Name", existingCategory.getCategoryName());
        assertTrue(existingCategory.isStatus());
        assertNotNull(existingCategory.getUpdatedAt());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void testUpdateCategoryNotFound() {
        Long id = 1L;
        Category categoryDetails = new Category();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(id, categoryDetails);
        });

        assertEquals("Cannot find category with id: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        Long id = 1L;
        Category category = new Category();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(id);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategoryNotFound() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(id);
        });

        assertEquals("Cannot find category with id: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(0)).delete(any(Category.class));
    }

    @Test
    void testCategoryExists() {
        String categoryName = "Existing Category";
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.of(new Category()));

        boolean result = categoryService.categoryExists(categoryName);

        assertTrue(result);
        verify(categoryRepository, times(1)).findByCategoryName(categoryName);
    }

    @Test
    void testCategoryDoesNotExist() {
        String categoryName = "Non-Existing Category";
        when(categoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.empty());

        boolean result = categoryService.categoryExists(categoryName);

        assertFalse(result);
        verify(categoryRepository, times(1)).findByCategoryName(categoryName);
    }
}
