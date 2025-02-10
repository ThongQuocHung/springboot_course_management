package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.SubCategory;
import group_2.cursus.service.SubCategoryService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubCategoryControllerTest {

    @InjectMocks
    private SubCategoryController subCategoryController;

    @Mock
    private SubCategoryService subCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSubCategories() {
        List<SubCategory> subCategories = Arrays.asList(new SubCategory(), new SubCategory());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SubCategory> page = new PageImpl<>(subCategories, pageable, subCategories.size());
        when(subCategoryService.getAllSubCategories(pageable)).thenReturn(page);

        ResponseEntity<APIResponse<Page<SubCategory>>> response = subCategoryController.getAllSubCategories(1, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().getContent().size());
    }

    @Test
    void testGetSubCategoryById() {
        Long subCategoryId = 1L;
        SubCategory subCategory = new SubCategory();
        when(subCategoryService.getSubCategoryById(subCategoryId)).thenReturn(subCategory);

        ResponseEntity<APIResponse<SubCategory>> response = subCategoryController.getSubCategoryById(subCategoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subCategory, response.getBody().getData());
    }

    @Test
    void testCreateSubCategory() {
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryName("Test SubCategory");

        when(subCategoryService.subCategoryExists(subCategory.getSubCategoryName())).thenReturn(false);
        when(subCategoryService.createSubCategory(subCategory)).thenReturn(subCategory);

        ResponseEntity<APIResponse<SubCategory>> response = subCategoryController.createSubCategory(subCategory);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("SubCategory created successfully", response.getBody().getMessage());
        assertEquals(subCategory, response.getBody().getData());
    }

    @Test
    void testCreateSubCategoryNameExists() {
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryName("Existing SubCategory");

        when(subCategoryService.subCategoryExists(subCategory.getSubCategoryName())).thenReturn(true);

        ResponseEntity<APIResponse<SubCategory>> response = subCategoryController.createSubCategory(subCategory);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("SubCategory name already exists", response.getBody().getMessage());
    }

    @Test
    void testUpdateSubCategory() {
        Long subCategoryId = 1L;
        SubCategory subCategoryDetails = new SubCategory();
        SubCategory updatedSubCategory = new SubCategory();

        when(subCategoryService.updateSubCategory(subCategoryId, subCategoryDetails)).thenReturn(updatedSubCategory);

        ResponseEntity<APIResponse<SubCategory>> response = subCategoryController.updateSubCategory(subCategoryId, subCategoryDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SubCategory updated successfully", response.getBody().getMessage());
        assertEquals(updatedSubCategory, response.getBody().getData());
    }

    @Test
    void testDeleteSubCategory() {
        Long subCategoryId = 1L;

        doNothing().when(subCategoryService).deleteSubCategory(subCategoryId);

        ResponseEntity<APIResponse<Void>> response = subCategoryController.deleteSubCategory(subCategoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("SubCategory deleted successfully", response.getBody().getMessage());
    }
}
