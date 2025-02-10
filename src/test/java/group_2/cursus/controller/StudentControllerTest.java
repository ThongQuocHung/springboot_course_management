package group_2.cursus.controller;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.CourseStudent;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Student;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private UUID studentId;
    private String email;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentId = UUID.fromString("91f01576-cbb2-41a0-b354-2af04f8d3752");
        email = "npb.danhg@gmail.com";

        SecurityContext securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(email);

        Student student = new Student();
        student.setId(studentId);
        student.setEmail(email);

        Instructor instructor = new Instructor();
        instructor.setId(UUID.randomUUID());

        course1 = new Course();
        course1.setCourseId(1L);
        course1.setPrice(new BigDecimal("89.99"));
        course1.setInstructor(instructor);

        course2 = new Course();
        course2.setCourseId(2L);
        course2.setPrice(new BigDecimal("59.99"));
        course2.setInstructor(instructor);

        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void testGetEnrolledCourses_Success() {
        List<Course> courses = Arrays.asList(course1, course2);

        when(studentService.getEnrolledCoursesByEmail(email)).thenReturn(courses);

        List<Course> response = studentController.getEnrolledCourses();

        assertEquals(2, response.size());
    }

    @Test
    void testEnrollCourse_Success() {
        Long courseId = 1L;
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setEnrolledAt(LocalDateTime.now());

        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        when(studentService.enrollCourse(studentId, courseId)).thenReturn(courseStudent);
        when(courseRepository.findCourseNameById(courseId)).thenReturn("CourseName");

        ResponseEntity<Map<String, Object>> response = studentController.enrollCourse(courseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("STUDENT: npb.danhg@gmail.com ENROLLED COURSE: CourseName", response.getBody().get("message"));
    }

    @Test
    void testEnrollCourse_CourseNotFound() {
        Long courseId = 1L;

        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        doThrow(new RuntimeException("Course not found")).when(studentService).enrollCourse(studentId, courseId);

        ResponseEntity<Map<String, Object>> response = studentController.enrollCourse(courseId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Course not found", response.getBody().get("message"));
    }

    @Test
    void testEnrollCourse_StudentHasntBoughtCourse() {
        Long courseId = 1L;

        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        doThrow(new RuntimeException("Student hasn't bought this course")).when(studentService).enrollCourse(studentId, courseId);

        ResponseEntity<Map<String, Object>> response = studentController.enrollCourse(courseId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Student hasn't bought this course", response.getBody().get("message"));
    }

    @Test
    void testEnrollCourse_StudentAlreadyEnrolled() {
        Long courseId = 1L;

        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        doThrow(new RuntimeException("Student already enrolled in this course")).when(studentService).enrollCourse(studentId, courseId);

        ResponseEntity<Map<String, Object>> response = studentController.enrollCourse(courseId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Student already enrolled in this course", response.getBody().get("message"));
    }

    @Test
    void testGetSubscribedInstructors_Success() {
        List<Instructor> instructors = Arrays.asList(new Instructor(), new Instructor());

        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        when(studentService.getSubscribedInstructors(studentId)).thenReturn(instructors);

        ResponseEntity<Map<String, Object>> response = studentController.getSubscribedInstructors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(instructors, response.getBody().get("instructors"));
    }

    @Test
    void testGetSubscribedInstructors_StudentNotFound() {
        when(studentService.getStudentIdByEmail(email)).thenReturn(studentId);
        doThrow(new RuntimeException("Student not found with ID: " + studentId)).when(studentService).getSubscribedInstructors(studentId);

        ResponseEntity<Map<String, Object>> response = studentController.getSubscribedInstructors();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found with ID: " + studentId, response.getBody().get("message"));
    }

    @Test
    public void testPurchaseCourses_Success() throws Exception {
        // Arrange
        when(studentService.purchaseCoursesByEmail(any(String.class), anyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/student/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Courses purchased successfully\"}"));
    }

    @Test
    public void testPurchaseCourses_AlreadyPurchased() throws Exception {
        // Arrange
        List<Long> alreadyPurchased = List.of(2L);
        when(studentService.purchaseCoursesByEmail(any(String.class), anyList())).thenReturn(alreadyPurchased);

        // Act & Assert
        mockMvc.perform(post("/student/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isConflict())
                .andExpect(content().json("{\"message\":\"Student has already purchased course with ID(s): [2]\"}"));
    }
}
