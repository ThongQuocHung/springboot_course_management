package group_2.cursus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LessonTest {

    private Lesson lesson;
    private Module module;

    @BeforeEach
    public void setUp() {
        module = new Module();
        module.setModuleName("Module");
        module.setCreatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        module.setUpdatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Sample Course");
        module.setCourse(course);

        Set<Lesson> lessons = new HashSet<>();
        lesson = new Lesson();
        lesson.setLessonName("Lesson");
        lesson.setDescription("Lesson's description.");
        lesson.setUrlVideo("http://newlesson.com/video");
        lesson.setCreatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        lesson.setUpdatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        lesson.setModule(module);

        lessons.add(lesson);
        module.setLessons(lessons);
    }

    @Test
    public void testLessonProperties() {
        assertNotNull(lesson);
        assertEquals("Lesson", lesson.getLessonName());
        assertEquals("Lesson's description.", lesson.getDescription());
        assertEquals("http://newlesson.com/video", lesson.getUrlVideo());
        assertEquals(LocalDateTime.of(2023, 7, 22, 12, 0), lesson.getCreatedAt());
        assertEquals(LocalDateTime.of(2023, 7, 22, 12, 0), lesson.getUpdatedAt());
        assertNotNull(lesson.getModule());
        assertEquals("Module", lesson.getModule().getModuleName());
        assertNotNull(lesson.getModule().getCourse());
        assertEquals(1L, lesson.getModule().getCourse().getCourseId());
        assertEquals("Sample Course", lesson.getModule().getCourse().getCourseName());
    }

    @Test
    public void testSettersAndGetters() {
        lesson.setLessonName("Updated Lesson");
        lesson.setDescription("Updated description");
        lesson.setUrlVideo("http://newlesson.com/updated_video");
        LocalDateTime newTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        lesson.setCreatedAt(newTime);
        lesson.setUpdatedAt(newTime);
        Module newModule = new Module();
        newModule.setModuleId(2L);
        newModule.setModuleName("Updated Module");
        lesson.setModule(newModule);

        assertEquals("Updated Lesson", lesson.getLessonName());
        assertEquals("Updated description", lesson.getDescription());
        assertEquals("http://newlesson.com/updated_video", lesson.getUrlVideo());
        assertEquals(newTime, lesson.getCreatedAt());
        assertEquals(newTime, lesson.getUpdatedAt());
        assertNotNull(lesson.getModule());
        assertEquals(2L, lesson.getModule().getModuleId());
        assertEquals("Updated Module", lesson.getModule().getModuleName());
    }
}
