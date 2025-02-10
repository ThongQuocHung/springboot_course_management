package group_2.cursus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModuleTest {

    private Module module;
    private Course course;

    @BeforeEach
    public void setUp() {
        module = new Module();
        module.setModuleName("Module");
        module.setCreatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        module.setUpdatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Course");

        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setLessonId(1L);
        lesson.setLessonName("Lesson");
        lesson.setDescription("Lesson's Description");
        lesson.setUrlVideo("http://newmodule.com/video");
        lesson.setCreatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        lesson.setUpdatedAt(LocalDateTime.of(2023, 7, 22, 12, 0));
        lesson.setModule(module);

        Set<Lesson> lessons = new HashSet<>();
        lessons.add(lesson);

        module.setLessons(lessons);
    }

    @Test
    public void testModuleProperties() {
        assertNotNull(module);
        assertEquals("Module", module.getModuleName());
        assertEquals(LocalDateTime.of(2023, 7, 22, 12, 0), module.getCreatedAt());
        assertEquals(LocalDateTime.of(2023, 7, 22, 12, 0), module.getUpdatedAt());
        assertNotNull(module.getCourse());
        assertEquals(1L, module.getCourse().getCourseId());
        assertEquals("Course", module.getCourse().getCourseName());
        assertNotNull(module.getLessons());
        assertEquals(1, module.getLessons().size());
    }

    @Test
    public void testSettersAndGetters() {
        module.setModuleName("Updated Module");
        LocalDateTime newTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        module.setCreatedAt(newTime);
        module.setUpdatedAt(newTime);

        Course newCourse = new Course();
        newCourse.setCourseId(2L);
        newCourse.setCourseName("Updated Course");
        module.setCourse(newCourse);

        Lesson newLesson = new Lesson();
        newLesson.setLessonId(2L);
        newLesson.setLessonName("Updated Lesson");
        newLesson.setDescription("Updated Description");
        newLesson.setUrlVideo("http://newmodule.com/updated_video");
        newLesson.setCreatedAt(newTime);
        newLesson.setUpdatedAt(newTime);
        newLesson.setModule(module);

        Set<Lesson> newLessons = new HashSet<>();
        newLessons.add(newLesson);
        module.setLessons(newLessons);

        assertEquals("Updated Module", module.getModuleName());
        assertEquals(newTime, module.getCreatedAt());
        assertEquals(newTime, module.getUpdatedAt());
        assertNotNull(module.getCourse());
        assertEquals(2L, module.getCourse().getCourseId());
        assertEquals("Updated Course", module.getCourse().getCourseName());
        assertNotNull(module.getLessons());
        assertEquals(1, module.getLessons().size());
        Lesson lesson = module.getLessons().iterator().next();
        assertEquals("Updated Lesson", lesson.getLessonName());
        assertEquals("Updated Description", lesson.getDescription());
        assertEquals("http://newmodule.com/updated_video", lesson.getUrlVideo());
        assertEquals(newTime, lesson.getCreatedAt());
        assertEquals(newTime, lesson.getUpdatedAt());
    }
}
