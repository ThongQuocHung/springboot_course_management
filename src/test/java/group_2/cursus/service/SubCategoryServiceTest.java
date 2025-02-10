package group_2.cursus.service;

import group_2.cursus.entity.Category;
import group_2.cursus.entity.SubCategory;
import group_2.cursus.repository.CategoryRepository;
import group_2.cursus.repository.SubCategoryRepository;
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

class SubCategoryServiceTest {

    @InjectMocks
    private SubCategoryService subCategoryService;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSubCategories() {
        List<SubCategory> subCategories = Arrays.asList(new SubCategory(), new SubCategory());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SubCategory> page = new PageImpl<>(subCategories, pageable, subCategories.size());
        when(subCategoryRepository.findAll(pageable)).thenReturn(page);

        Page<SubCategory> result = subCategoryService.getAllSubCategories(pageable);

        assertEquals(subCategories, result.getContent());
        verify(subCategoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetSubCategoryById() {
        Long id = 1L;
        SubCategory subCategory = new SubCategory();
        when(subCategoryRepository.findById(id)).thenReturn(Optional.of(subCategory));

        SubCategory result = subCategoryService.getSubCategoryById(id);

        assertEquals(subCategory, result);
        verify(subCategoryRepository, times(1)).findById(id);
    }

    @Test
    void testGetSubCategoryByIdNotFound() {
        Long id = 1L;
        when(subCategoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subCategoryService.getSubCategoryById(id);
        });

        assertEquals("Cannot find subcategory with id: " + id, exception.getMessage());
        verify(subCategoryRepository, times(1)).findById(id);
    }

    @Test
    void testCreateSubCategory() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        SubCategory subCategory = new SubCategory();
        subCategory.setCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(subCategoryRepository.save(subCategory)).thenReturn(subCategory);

        SubCategory result = subCategoryService.createSubCategory(subCategory);

        assertEquals(subCategory, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(subCategoryRepository, times(1)).save(subCategory);
    }

    @Test
    void testCreateSubCategoryCategoryNotFound() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        SubCategory subCategory = new SubCategory();
        subCategory.setCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subCategoryService.createSubCategory(subCategory);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(subCategoryRepository, times(0)).save(subCategory);
    }

    @Test
    void testUpdateSubCategory() {
        Long id = 1L;
        SubCategory existingSubCategory = new SubCategory();
        SubCategory subCategoryDetails = new SubCategory();
        subCategoryDetails.setSubCategoryName("Updated Name");
        subCategoryDetails.setStatus(true);

        when(subCategoryRepository.findById(id)).thenReturn(Optional.of(existingSubCategory));
        when(subCategoryRepository.save(existingSubCategory)).thenReturn(existingSubCategory);

        SubCategory result = subCategoryService.updateSubCategory(id, subCategoryDetails);

        assertEquals(existingSubCategory, result);
        assertEquals("Updated Name", existingSubCategory.getSubCategoryName());
        assertTrue(existingSubCategory.isStatus());
        assertNotNull(existingSubCategory.getUpdatedAt());
        verify(subCategoryRepository, times(1)).findById(id);
        verify(subCategoryRepository, times(1)).save(existingSubCategory);
    }

    @Test
    void testUpdateSubCategoryNotFound() {
        Long id = 1L;
        SubCategory subCategoryDetails = new SubCategory();
        when(subCategoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subCategoryService.updateSubCategory(id, subCategoryDetails);
        });

        assertEquals("Cannot find subcategory with id: " + id, exception.getMessage());
        verify(subCategoryRepository, times(1)).findById(id);
        verify(subCategoryRepository, times(0)).save(any(SubCategory.class));
    }

    @Test
    void testDeleteSubCategory() {
        Long id = 1L;
        SubCategory subCategory = new SubCategory();
        when(subCategoryRepository.findById(id)).thenReturn(Optional.of(subCategory));

        subCategoryService.deleteSubCategory(id);

        verify(subCategoryRepository, times(1)).findById(id);
        verify(subCategoryRepository, times(1)).delete(subCategory);
    }

    @Test
    void testDeleteSubCategoryNotFound() {
        Long id = 1L;
        when(subCategoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subCategoryService.deleteSubCategory(id);
        });

        assertEquals("Cannot find subcategory with id: " + id, exception.getMessage());
        verify(subCategoryRepository, times(1)).findById(id);
        verify(subCategoryRepository, times(0)).delete(any(SubCategory.class));
    }

    @Test
    void testSubCategoryExists() {
        String subCategoryName = "Existing SubCategory";
        when(subCategoryRepository.findBySubCategoryName(subCategoryName)).thenReturn(Optional.of(new SubCategory()));

        boolean result = subCategoryService.subCategoryExists(subCategoryName);

        assertTrue(result);
        verify(subCategoryRepository, times(1)).findBySubCategoryName(subCategoryName);
    }

    @Test
    void testSubCategoryDoesNotExist() {
        String subCategoryName = "Non-Existing SubCategory";
        when(subCategoryRepository.findBySubCategoryName(subCategoryName)).thenReturn(Optional.empty());

        boolean result = subCategoryService.subCategoryExists(subCategoryName);

        assertFalse(result);
        verify(subCategoryRepository, times(1)).findBySubCategoryName(subCategoryName);
    }
}
