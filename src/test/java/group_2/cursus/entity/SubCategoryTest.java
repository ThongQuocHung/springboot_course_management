package group_2.cursus.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SubCategoryTest {

    @Test
    void testSubCategoryCreation() {
        Category category = new Category();
        SubCategory subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setSubCategoryName("Test SubCategory");

        assertThat(subCategory.getSubCategoryName()).isEqualTo("Test SubCategory");
        assertThat(subCategory.getCategory()).isEqualTo(category);
    }

    @Test
    void testSubCategoryMethods() {
        SubCategory subCategory = new SubCategory();
        Category category = new Category();

        long id = 1L;
        subCategory.setSubCategoryId(id);
        subCategory.setSubCategoryName("Test SubCategory");
        subCategory.setStatus(true);
        subCategory.setCategory(category);

        LocalDateTime now = LocalDateTime.now();
        subCategory.setCreatedAt(now);
        subCategory.setUpdatedAt(now);

        assertThat(subCategory.getSubCategoryId()).isEqualTo(id);
        assertThat(subCategory.getSubCategoryName()).isEqualTo("Test SubCategory");
        assertThat(subCategory.isStatus()).isTrue();
        assertThat(subCategory.getCategory()).isEqualTo(category);
        assertThat(subCategory.getCreatedAt()).isEqualTo(now);
        assertThat(subCategory.getUpdatedAt()).isEqualTo(now);
    }

}
