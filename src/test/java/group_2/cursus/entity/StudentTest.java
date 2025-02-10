package group_2.cursus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

public class StudentTest {

    private Student student;

    @BeforeEach
    public void setUp() {
        student = new Student("student@gmail.com", "password", "Danh Handsome", true);
        student.setMajor("Computer Science");
        student.setActive(true);

        CourseStudent courseStudent = new CourseStudent();
        Set<CourseStudent> courseStudents = new HashSet<>();
        courseStudents.add(courseStudent);

        student.setCourseStudents(courseStudents);
    }

    @Test
    public void testStudentProperties() {
        assertNotNull(student);
        assertEquals("student@gmail.com", student.getEmail());
        assertEquals("password", student.getPassword());
        assertEquals("Danh Handsome", student.getFullName());
        assertTrue(student.isGender());
        assertEquals("Computer Science", student.getMajor());
        assertTrue(student.isActive());
        assertNotNull(student.getCourseStudents());
        assertEquals(1, student.getCourseStudents().size());
    }

    @Test
    public void testSettersAndGetters() {
        student.setMajor("Data Science");
        student.setActive(false);

        CourseStudent newCourseStudent = new CourseStudent();
        Set<CourseStudent> newCourseStudents = new HashSet<>();
        newCourseStudents.add(newCourseStudent);
        student.setCourseStudents(newCourseStudents);

        assertEquals("Data Science", student.getMajor());
        assertTrue(!student.isActive());
        assertNotNull(student.getCourseStudents());
        assertEquals(1, student.getCourseStudents().size());
    }

    @Test
    public void testGetAuthorities() {
        assertNotNull(student.getAuthorities());
        assertEquals(1, student.getAuthorities().size());
        GrantedAuthority authority = student.getAuthorities().iterator().next();
        assertEquals("STUDENT", authority.getAuthority());
    }
}
