package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseStudentTest {

    private CourseStudent courseStudent;
    private CourseStudentId courseStudentId;
    private Course course;
    private Student student;

    @BeforeEach
    void setUp() {
        courseStudent = new CourseStudent();

        courseStudentId = new CourseStudentId();
        courseStudentId.setCourseId(1L);

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        student = new Student("test@student.com", "password", "Test Student", true);
        student.setMajor("Computer Science");
        student.setActive(true);
    }

    @Test
    void testGetId() {
        courseStudent.setId(courseStudentId);
        assertThat(courseStudent.getId()).isEqualTo(courseStudentId);
    }

    @Test
    void testSetId() {
        CourseStudentId newId = new CourseStudentId();
        newId.setCourseId(2L);
        courseStudent.setId(newId);
        assertThat(courseStudent.getId()).isEqualTo(newId);
    }

    @Test
    void testIsPaid() {
        courseStudent.setPaid(true);
        assertThat(courseStudent.isPaid()).isTrue();
    }

    @Test
    void testSetPaid() {
        courseStudent.setPaid(false);
        assertThat(courseStudent.isPaid()).isFalse();
    }

    @Test
    void testGetEnrolledAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setEnrolledAt(now);
        assertThat(courseStudent.getEnrolledAt()).isEqualTo(now);
    }

    @Test
    void testSetEnrolledAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setEnrolledAt(now);
        assertThat(courseStudent.getEnrolledAt()).isEqualTo(now);
    }

    @Test
    void testGetPurchasedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setPurchasedAt(now);
        assertThat(courseStudent.getPurchasedAt()).isEqualTo(now);
    }

    @Test
    void testSetPurchasedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setPurchasedAt(now);
        assertThat(courseStudent.getPurchasedAt()).isEqualTo(now);
    }

    @Test
    void testGetSavedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setSavedAt(now);
        assertThat(courseStudent.getSavedAt()).isEqualTo(now);
    }

    @Test
    void testSetSavedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setSavedAt(now);
        assertThat(courseStudent.getSavedAt()).isEqualTo(now);
    }

    @Test
    void testIsLiked() {
        courseStudent.setLiked(true);
        assertThat(courseStudent.isLiked()).isTrue();
    }

    @Test
    void testSetLiked() {
        courseStudent.setLiked(false);
        assertThat(courseStudent.isLiked()).isFalse();
    }

    @Test
    void testGetRating() {
        courseStudent.setRating(4);
        assertThat(courseStudent.getRating()).isEqualTo(4);
    }

    @Test
    void testSetRating() {
        courseStudent.setRating(5);
        assertThat(courseStudent.getRating()).isEqualTo(5);
    }

    @Test
    void testGetReview() {
        courseStudent.setReview("Great course!");
        assertThat(courseStudent.getReview()).isEqualTo("Great course!");
    }

    @Test
    void testSetReview() {
        courseStudent.setReview("Needs improvement.");
        assertThat(courseStudent.getReview()).isEqualTo("Needs improvement.");
    }

    @Test
    void testGetReviewedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setReviewedAt(now);
        assertThat(courseStudent.getReviewedAt()).isEqualTo(now);
    }

    @Test
    void testSetReviewedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseStudent.setReviewedAt(now);
        assertThat(courseStudent.getReviewedAt()).isEqualTo(now);
    }

    @Test
    void testGetStudent() {
        courseStudent.setStudent(student);
        assertThat(courseStudent.getStudent()).isEqualTo(student);
    }

    @Test
    void testSetStudent() {
        Student newStudent = new Student("new@student.com", "newpassword", "New Student", false);
        newStudent.setMajor("Mathematics");
        newStudent.setActive(false);
        courseStudent.setStudent(newStudent);
        assertThat(courseStudent.getStudent()).isEqualTo(newStudent);
    }

    @Test
    void testGetCourse() {
        courseStudent.setCourse(course);
        assertThat(courseStudent.getCourse()).isEqualTo(course);
    }

    @Test
    void testSetCourse() {
        Course newCourse = new Course();
        newCourse.setCourseId(2L);
        newCourse.setCourseName("New Course");
        courseStudent.setCourse(newCourse);
        assertThat(courseStudent.getCourse()).isEqualTo(newCourse);
    }
}
