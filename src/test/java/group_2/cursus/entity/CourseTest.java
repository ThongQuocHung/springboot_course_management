package group_2.cursus.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest {

    @Test
    void testCourseCreation() {
        SubCategory subCategory = new SubCategory();
        Instructor instructor = new Instructor();
        Course course = new Course();
        course.setCourseName("Test Course");
        course.setDescription("This is a test course description.");
        course.setPrice(new BigDecimal("100.00"));
        course.setThumb("thumb.jpg");
        course.setView(0);
        course.setActive("PENDING");
        course.setStatus(true);
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);

        assertThat(course.getCourseName()).isEqualTo("Test Course");
        assertThat(course.getDescription()).isEqualTo("This is a test course description.");
        assertThat(course.getPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(course.getThumb()).isEqualTo("thumb.jpg");
        assertThat(course.getView()).isEqualTo(0);
        assertThat(course.getIsActive()).isEqualTo("PENDING");
        assertThat(course.isStatus()).isTrue();
        assertThat(course.getSubCategory()).isEqualTo(subCategory);
        assertThat(course.getInstructor()).isEqualTo(instructor);
    }

    @Test
    void testCourseMethods() {
        Course course = new Course();
        SubCategory subCategory = new SubCategory();
        Instructor instructor = new Instructor();

        long id = 1L;
        course.setCourseId(id);
        course.setCourseName("Test Course");
        course.setDescription("This is a test course description.");
        course.setPrice(new BigDecimal("100.00"));
        course.setThumb("thumb.jpg");
        course.setView(10);
        course.setActive("APPROVED");
        course.setStatus(true);
        course.setSubCategory(subCategory);
        course.setInstructor(instructor);

        LocalDateTime now = LocalDateTime.now();
        course.setCreateAt(now);
        course.setUpdatedAt(now);

        assertThat(course.getCourseId()).isEqualTo(id);
        assertThat(course.getCourseName()).isEqualTo("Test Course");
        assertThat(course.getDescription()).isEqualTo("This is a test course description.");
        assertThat(course.getPrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(course.getThumb()).isEqualTo("thumb.jpg");
        assertThat(course.getView()).isEqualTo(10);
        assertThat(course.getIsActive()).isEqualTo("APPROVED");
        assertThat(course.isStatus()).isTrue();
        assertThat(course.getSubCategory()).isEqualTo(subCategory);
        assertThat(course.getInstructor()).isEqualTo(instructor);
        assertThat(course.getCreatedAt()).isEqualTo(now);
        assertThat(course.getUpdatedAt()).isEqualTo(now);
    }
}
