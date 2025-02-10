package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void testGetCategoryId() {
        long categoryId = 1L;
        category.setCategoryId(categoryId);
        assertEquals(categoryId, category.getCategoryId());
    }

    @Test
    void testSetCategoryId() {
        long categoryId = 2L;
        category.setCategoryId(categoryId);
        assertEquals(categoryId, category.getCategoryId());
    }

    @Test
    void testGetCategoryName() {
        String categoryName = "Test Category";
        category.setCategoryName(categoryName);
        assertEquals(categoryName, category.getCategoryName());
    }

    @Test
    void testSetCategoryName() {
        String categoryName = "New Category";
        category.setCategoryName(categoryName);
        assertEquals(categoryName, category.getCategoryName());
    }

    @Test
    void testIsStatus() {
        category.setStatus(true);
        assertTrue(category.isStatus());

        category.setStatus(false);
        assertFalse(category.isStatus());
    }

    @Test
    void testSetStatus() {
        category.setStatus(false);
        assertFalse(category.isStatus());
    }

    @Test
    void testGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        category.setCreatedAt(createdAt);
        assertEquals(createdAt, category.getCreatedAt());
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        category.setCreatedAt(createdAt);
        assertEquals(createdAt, category.getCreatedAt());
    }

    @Test
    void testGetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        category.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, category.getUpdatedAt());
    }

    @Test
    void testSetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        category.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, category.getUpdatedAt());
    }

    @Test
    void testDefaultValues() {
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertTrue(category.isStatus());
    }
}
