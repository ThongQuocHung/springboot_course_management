package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseReviewTest {

    private CourseReview courseReview;
    private Course course;
    private Student student;

    @BeforeEach
    void setUp() {
        courseReview = new CourseReview();

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        student = new Student("test@student.com", "password", "Test Student", true);
        student.setMajor("Computer Science");
        student.setActive(true);
    }

    @Test
    void testGetId() {
        courseReview.setId(1L);
        assertThat(courseReview.getId()).isEqualTo(1L);
    }

    @Test
    void testSetId() {
        courseReview.setId(2L);
        assertThat(courseReview.getId()).isEqualTo(2L);
    }

    @Test
    void testGetCourse() {
        courseReview.setCourse(course);
        assertThat(courseReview.getCourse()).isEqualTo(course);
    }

    @Test
    void testSetCourse() {
        Course newCourse = new Course();
        newCourse.setCourseId(2L);
        newCourse.setCourseName("New Course");
        courseReview.setCourse(newCourse);
        assertThat(courseReview.getCourse()).isEqualTo(newCourse);
    }

    @Test
    void testGetStudent() {
        courseReview.setStudent(student);
        assertThat(courseReview.getStudent()).isEqualTo(student);
    }

    @Test
    void testSetStudent() {
        Student newStudent = new Student("new@student.com", "newpassword", "New Student", false);
        newStudent.setMajor("Mathematics");
        newStudent.setActive(false);
        courseReview.setStudent(newStudent);
        assertThat(courseReview.getStudent()).isEqualTo(newStudent);
    }

    @Test
    void testGetRating() {
        courseReview.setRating(5);
        assertThat(courseReview.getRating()).isEqualTo(5);
    }

    @Test
    void testSetRating() {
        courseReview.setRating(4);
        assertThat(courseReview.getRating()).isEqualTo(4);
    }

    @Test
    void testGetComment() {
        courseReview.setComment("Great course!");
        assertThat(courseReview.getComment()).isEqualTo("Great course!");
    }

    @Test
    void testSetComment() {
        courseReview.setComment("Needs improvement.");
        assertThat(courseReview.getComment()).isEqualTo("Needs improvement.");
    }

    @Test
    void testGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseReview.setCreatedAt(now);
        assertThat(courseReview.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseReview.setCreatedAt(now);
        assertThat(courseReview.getCreatedAt()).isEqualTo(now);
    }
}
