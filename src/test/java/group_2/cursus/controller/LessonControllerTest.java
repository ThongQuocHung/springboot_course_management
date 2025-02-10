package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Lesson;
import group_2.cursus.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    private Lesson lesson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lesson = new Lesson();
        lesson.setLessonId(1L);
        lesson.setLessonName("Test Lesson");
        lesson.setDescription("This is a test lesson.");
        lesson.setUrlVideo("http://test.url/video");
    }

    @Test
    void testGetAllLesson() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Lesson> lessonPage = new PageImpl<>(Collections.singletonList(lesson));
        when(lessonService.getAllLesson(anyLong(), any(Pageable.class))).thenReturn(lessonPage);

        ResponseEntity<APIResponse<Page<Lesson>>> response = lessonController.getAllLesson(1L, 1, 5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(lessonPage);
    }

    @Test
    void testGetLessonById() {
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        ResponseEntity<APIResponse<Lesson>> response = lessonController.getLessonById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(lesson);
    }

    @Test
    void testCreateLesson() {
        when(lessonService.createLesson(anyLong(), any(Lesson.class))).thenReturn(lesson);

        ResponseEntity<APIResponse<Lesson>> response = lessonController.createLesson(1L, lesson);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(lesson);
    }

    @Test
    void testUpdateLesson() {
        when(lessonService.updateLesson(anyLong(), any(Lesson.class))).thenReturn(lesson);

        ResponseEntity<APIResponse<Lesson>> response = lessonController.updateLesson(1L, 1L, lesson);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(lesson);
    }

    @Test
    void testDeleteLesson() {
        when(lessonService.deleteLesson(anyLong())).thenReturn(lesson);

        ResponseEntity<APIResponse<Lesson>> response = lessonController.deleteLesson(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo(lesson);
    }
}
