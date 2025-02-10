package group_2.cursus.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CourseStudentIdTest {

    private CourseStudentId courseStudentId1;
    private CourseStudentId courseStudentId2;
    private UUID studentId;
    private long courseId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        courseId = 1L;
        courseStudentId1 = new CourseStudentId(studentId, courseId);
        courseStudentId2 = new CourseStudentId();
    }

    @Test
    void testGetStudentId() {
        assertThat(courseStudentId1.getStudentId()).isEqualTo(studentId);
    }

    @Test
    void testSetStudentId() {
        UUID newStudentId = UUID.randomUUID();
        courseStudentId2.setStudentId(newStudentId);
        assertThat(courseStudentId2.getStudentId()).isEqualTo(newStudentId);
    }

    @Test
    void testGetCourseId() {
        assertThat(courseStudentId1.getCourseId()).isEqualTo(courseId);
    }

    @Test
    void testSetCourseId() {
        long newCourseId = 2L;
        courseStudentId2.setCourseId(newCourseId);
        assertThat(courseStudentId2.getCourseId()).isEqualTo(newCourseId);
    }

    @Test
    void testEquals() {
        CourseStudentId sameCourseStudentId = new CourseStudentId(studentId, courseId);
        CourseStudentId differentCourseStudentId = new CourseStudentId(UUID.randomUUID(), 2L);

        assertEquals(courseStudentId1, sameCourseStudentId);
        assertNotEquals(courseStudentId1, differentCourseStudentId);
        assertNotEquals(courseStudentId1, null);
        assertNotEquals(courseStudentId1, new Object());
    }

    @Test
    void testHashCode() {
        CourseStudentId sameCourseStudentId = new CourseStudentId(studentId, courseId);
        assertEquals(courseStudentId1.hashCode(), sameCourseStudentId.hashCode());

        CourseStudentId differentCourseStudentId = new CourseStudentId(UUID.randomUUID(), 2L);
        assertNotEquals(courseStudentId1.hashCode(), differentCourseStudentId.hashCode());
    }
}
