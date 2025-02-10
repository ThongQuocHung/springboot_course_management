package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstructorTest {

    private Instructor instructor;
    private String email;
    private String password;
    private String fullName;
    private boolean gender;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password";
        fullName = "Test User";
        gender = true;
        instructor = new Instructor(email, password, fullName, gender);
    }

    @Test
    void testGetAndSetMajor() {
        String major = "Computer Science";
        instructor.setMajor(major);
        assertEquals(major, instructor.getMajor());
    }

    @Test
    void testGetAndSetAbout() {
        String about = "Experienced instructor in computer science.";
        instructor.setAbout(about);
        assertEquals(about, instructor.getAbout());
    }

    @Test
    void testIsActive() {
        assertFalse(instructor.isActive());
        instructor.setActive(true);
        assertTrue(instructor.isActive());
    }

    @Test
    void testSetAndGetCourses() {
        Set<Course> courses = new HashSet<>();
        Course course1 = new Course();
        Course course2 = new Course();
        courses.add(course1);
        courses.add(course2);
        instructor.setCourses(courses);
        assertEquals(courses, instructor.getCourses());
    }

    @Test
    void testSetAndGetPayouts() {
        Set<Payout> payouts = new HashSet<>();
        Payout payout1 = new Payout();
        Payout payout2 = new Payout();
        payouts.add(payout1);
        payouts.add(payout2);
        instructor.setPayouts(payouts);
        assertEquals(payouts, instructor.getPayouts());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = instructor.getAuthorities();
        assertEquals(1, authorities.size());
        assertThat(authorities).extracting("authority").contains("INSTRUCTOR");
    }
}
