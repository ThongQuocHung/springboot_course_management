package group_2.cursus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import group_2.cursus.entity.Lesson;
import group_2.cursus.entity.Module;
import group_2.cursus.repository.LessonRepository;
import group_2.cursus.repository.ModuleRepository;

public class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ModuleRepository moduleRepository;

    private Lesson lesson;
    private Module module;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        module = new Module();
        module.setModuleId(1L);
        module.setModuleName("Module 1");

        lesson = new Lesson();
        lesson.setLessonId(1L);
        lesson.setLessonName("Lesson 1");
        lesson.setDescription("Description");
        lesson.setUrlVideo("http://video.url");
        lesson.setModule(module);
    }

    @Test
    public void testGetAllLesson_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> lessonPage = new PageImpl<>(Set.of(lesson).stream().collect(Collectors.toList()));

        when(lessonRepository.findAll(pageable)).thenReturn(lessonPage);

        Page<Lesson> result = lessonService.getAllLesson(module.getModuleId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(lessonRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetAllLesson_Failure() {
        Pageable pageable = PageRequest.of(0, 10);
        when(lessonRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.getAllLesson(module.getModuleId(), pageable);
        });

        assertEquals("Failed to get all lesson: Database error", exception.getMessage());
        verify(lessonRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetLessonById_Success() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.getLessonById(lesson.getLessonId());

        assertNotNull(result);
        assertEquals(lesson.getLessonId(), result.getLessonId());
        verify(lessonRepository, times(1)).findById(lesson.getLessonId());
    }

    @Test
    public void testGetLessonById_NotFound() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.getLessonById(1L);
        });

        assertEquals("Failed to get lesson by id: 1", exception.getMessage());
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateLesson_Success() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson newLesson = new Lesson();
        newLesson.setLessonName("Lesson 1");
        newLesson.setDescription("New description");
        newLesson.setUrlVideo("http://newvideo.url");

        Lesson result = lessonService.createLesson(module.getModuleId(), newLesson);

        assertNotNull(result);
        assertEquals(newLesson.getLessonName(), result.getLessonName());
        assertEquals(module, result.getModule());
        verify(moduleRepository, times(1)).findById(module.getModuleId());
        verify(lessonRepository, times(1)).save(newLesson);
    }

    @Test
    public void testUpdateLesson_Success() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson updatedLesson = new Lesson();
        updatedLesson.setLessonName("Updated Lesson");
        updatedLesson.setDescription("Updated Description");
        updatedLesson.setUrlVideo("http://updatedvideo.url");

        Lesson result = lessonService.updateLesson(lesson.getLessonId(), updatedLesson);

        assertNotNull(result);
        assertEquals(updatedLesson.getLessonName(), result.getLessonName());
        assertEquals(updatedLesson.getDescription(), result.getDescription());
        assertEquals(updatedLesson.getUrlVideo(), result.getUrlVideo());
        verify(lessonRepository, times(1)).findById(lesson.getLessonId());
        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    public void testUpdateLesson_NotFound() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        Lesson updatedLesson = new Lesson();
        updatedLesson.setLessonName("Updated Lesson");
        updatedLesson.setDescription("Updated Description");
        updatedLesson.setUrlVideo("http://updatedvideo.url");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.updateLesson(1L, updatedLesson);
        });

        assertEquals("Failed to update lesson: Lesson not found with id: 1", exception.getMessage());
        verify(lessonRepository, times(1)).findById(1L);
        verify(lessonRepository, times(0)).save(any(Lesson.class));
    }

    @Test
    public void testDeleteLesson_Success() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.deleteLesson(lesson.getLessonId());

        assertNotNull(result);
        assertEquals(lesson.getLessonId(), result.getLessonId());
        verify(lessonRepository, times(1)).findById(lesson.getLessonId());
        verify(lessonRepository, times(1)).delete(lesson);
    }

    @Test
    public void testDeleteLesson_NotFound() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lessonService.deleteLesson(1L);
        });

        assertEquals("Failed to delete lesson: Lesson not found with id: 1", exception.getMessage());
        verify(lessonRepository, times(1)).findById(1L);
        verify(lessonRepository, times(0)).delete(any(Lesson.class));
    }
}
