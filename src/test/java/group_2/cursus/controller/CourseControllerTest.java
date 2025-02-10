package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Course;
import group_2.cursus.entity.CourseReport;
import group_2.cursus.entity.CourseReview;
import group_2.cursus.entity.Student;
import group_2.cursus.repository.StudentRepository;
import group_2.cursus.service.CourseService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testGetAllCourses() {
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseService.getAllCourses(pageable)).thenReturn(coursePage);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.getAllCourses(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Courses retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
        verify(courseService, times(1)).getAllCourses(pageable);
    }

    @Test
    void testSearchCourses() {
        String courseName = "Java";
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseService.searchCoursesByName(courseName, pageable)).thenReturn(coursePage);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.searchCourses(courseName, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Courses found successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
    }

    @Test
    void testSearchCoursesNoResults() {
        String courseName = "NonExistentCourse";
        Page<Course> emptyPage = Page.empty();
        Pageable pageable = PageRequest.of(0, 10);

        when(courseService.searchCoursesByName(courseName, pageable)).thenReturn(emptyPage);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.searchCourses(courseName, 0, 10);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No courses found with the given name", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void testGetCoursesByCategory() {
        Long subcategoryId = 1L;
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseService.getCoursesBySubCategory(subcategoryId, pageable)).thenReturn(coursePage);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.getCoursesByCategory(subcategoryId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Courses for category retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
    }

    @Test
    void testGetEnrolledCourses() {
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        student.setId(studentId);
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(authentication.getName()).thenReturn("student@example.com");
        when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(courseService.getEnrolledCourses(student, pageable)).thenReturn(coursePage);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.getEnrolledCourses(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().getContent().size());
        assertTrue(response.getBody().getMessage().contains("Enrolled courses for student"));
    }

    @Test
    void testAddReview() {
        Long courseId = 1L;
        int rating = 5;
        String comment = "Great course!";
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        student.setId(studentId);
        CourseReview review = new CourseReview();

        when(authentication.getName()).thenReturn("student@example.com");
        when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(courseService.addCourseReview(courseId, studentId, rating, comment)).thenReturn(review);

        ResponseEntity<APIResponse<CourseReview>> response = courseController.addReview(courseId, rating, comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Review added successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetReviews() {
        Long courseId = 1L;
        List<CourseReview> reviews = Arrays.asList(new CourseReview(), new CourseReview());

        when(courseService.getCourseReviews(courseId)).thenReturn(reviews);

        ResponseEntity<APIResponse<List<CourseReview>>> response = courseController.getReviews(courseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reviews retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testReportCourse() {
        Long courseId = 1L;
        String reason = "Inappropriate content";
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        student.setId(studentId);
        CourseReport report = new CourseReport();

        when(authentication.getName()).thenReturn("student@example.com");
        when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(courseService.reportCourse(courseId, studentId, reason)).thenReturn(report);

        ResponseEntity<APIResponse<CourseReport>> response = courseController.reportCourse(courseId, reason);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course reported successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }
}