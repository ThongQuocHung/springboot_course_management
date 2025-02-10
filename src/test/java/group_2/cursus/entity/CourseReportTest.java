package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CourseReportTest {

    private CourseReport courseReport;
    private Course course;
    private Student student;

    @BeforeEach
    void setUp() {
        courseReport = new CourseReport();

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        student = new Student("test@student.com", "password", "Test Student", true);
        student.setMajor("Computer Science");
        student.setActive(true);
    }

    @Test
    void testGetId() {
        courseReport.setId(1L);
        assertThat(courseReport.getId()).isEqualTo(1L);
    }

    @Test
    void testSetId() {
        courseReport.setId(2L);
        assertThat(courseReport.getId()).isEqualTo(2L);
    }

    @Test
    void testGetCourse() {
        courseReport.setCourse(course);
        assertThat(courseReport.getCourse()).isEqualTo(course);
    }

    @Test
    void testSetCourse() {
        Course newCourse = new Course();
        newCourse.setCourseId(2L);
        newCourse.setCourseName("New Course");
        courseReport.setCourse(newCourse);
        assertThat(courseReport.getCourse()).isEqualTo(newCourse);
    }

    @Test
    void testGetStudent() {
        courseReport.setStudent(student);
        assertThat(courseReport.getStudent()).isEqualTo(student);
    }

    @Test
    void testSetStudent() {
        Student newStudent = new Student("new@student.com", "newpassword", "New Student", false);
        newStudent.setMajor("Mathematics");
        newStudent.setActive(false);
        courseReport.setStudent(newStudent);
        assertThat(courseReport.getStudent()).isEqualTo(newStudent);
    }

    @Test
    void testGetReason() {
        courseReport.setReason("Test Reason");
        assertThat(courseReport.getReason()).isEqualTo("Test Reason");
    }

    @Test
    void testSetReason() {
        courseReport.setReason("New Reason");
        assertThat(courseReport.getReason()).isEqualTo("New Reason");
    }

    @Test
    void testGetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseReport.setCreatedAt(now);
        assertThat(courseReport.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        courseReport.setCreatedAt(now);
        assertThat(courseReport.getCreatedAt()).isEqualTo(now);
    }
}
