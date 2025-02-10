package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Course;
import group_2.cursus.entity.CourseReview;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.InstructorRepository;
import group_2.cursus.service.CourseService;
import group_2.cursus.service.InstructorService;
import group_2.cursus.service.JwtService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InstructorControllerTest {

    @InjectMocks
    private InstructorController instructorController;

    @Mock
    private CourseService courseService;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private InstructorService instructorService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private InstructorController courseController;

    private MockMvc mockMvc;
    private UUID instructorId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(instructorController).build();

        // Mock Authentication and SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        instructorId = UUID.randomUUID();
        when(jwtService.extractEmail(any(String.class))).thenReturn("instructor@gmail.com");
        when(instructorService.getInstructorIdByEmail(any(String.class))).thenReturn(instructorId);
    }

    @Test
    void testGetInstructorReviews_Success() {
        UUID instructorId = UUID.randomUUID();
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        List<CourseReview> reviews = Arrays.asList(new CourseReview(), new CourseReview());

        when(authentication.getName()).thenReturn("instructor@gmail.com");
        when(instructorRepository.findByEmail("instructor@gmail.com")).thenReturn(Optional.of(instructor));
        when(courseService.getInstructorReviews(instructorId)).thenReturn(reviews);

        ResponseEntity<APIResponse<List<CourseReview>>> response = instructorController.getInstructorReviews(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Instructor reviews retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetInstructorReviews_InstructorNotFound() {
        when(authentication.getName()).thenReturn("nonexistent@gmail.com");
        when(instructorRepository.findByEmail("nonexistent@gmail.com")).thenReturn(Optional.empty());

        ResponseEntity<APIResponse<List<CourseReview>>> response = instructorController.getInstructorReviews(authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Instructor not found", response.getBody().getMessage());
    }

    @Test
    void testCreateCourse() {
        Course course = new Course();
        course.setCourseName("New Course");
        Course createdCourse = new Course();

        when(courseService.courseExists(course.getCourseName())).thenReturn(false);
        when(courseService.createCourse(instructorId, course)).thenReturn(createdCourse);

        ResponseEntity<APIResponse<Course>> response = instructorController.createCourse("Bearer token", course);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Course created successfully", response.getBody().getMessage());
        assertEquals(createdCourse, response.getBody().getData());
    }

    @Test
    void testCreateCourseNameExists() {
        Course course = new Course();
        course.setCourseName("Existing Course");

        when(courseService.courseExists(course.getCourseName())).thenReturn(true);

        ResponseEntity<APIResponse<Course>> response = instructorController.createCourse("Bearer token", course);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Course name already exists", response.getBody().getMessage());
    }

    @Test
    void testGetCoursesByInstructor() {
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Course> page = new PageImpl<>(courseList, pageable, courseList.size());
        when(courseService.getCoursesByInstructor(instructorId, pageable)).thenReturn(page);

        ResponseEntity<APIResponse<Page<Course>>> response = courseController.getCoursesByInstructor("Bearer token", 1, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().getContent().size());
    }

    @Test
    void testGetCourseById() {
        Long courseId = 1L;
        Course course = new Course();

        when(courseService.getCourseByIdAndInstructor(instructorId, courseId)).thenReturn(course);

        ResponseEntity<APIResponse<Course>> response = instructorController.getCourseById("Bearer token", courseId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody().getData());
    }

    @Test
    void testUpdateCourse() {
        Long courseId = 1L;
        Course courseDetails = new Course();
        Course updatedCourse = new Course();

        when(courseService.updateCourse(instructorId, courseId, courseDetails)).thenReturn(updatedCourse);

        ResponseEntity<APIResponse<Course>> response = instructorController.updateCourse("Bearer token", courseId, courseDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course updated successfully", response.getBody().getMessage());
        assertEquals(updatedCourse, response.getBody().getData());
    }

    @Test
    void testDeleteCourse() {
        Long courseId = 1L;

        doNothing().when(courseService).deleteCourse(instructorId, courseId);

        ResponseEntity<?> response = instructorController.deleteCourse("Bearer token", courseId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testResubmitCourse() {
        Long courseId = 1L;
        Course courseDetails = new Course();
        Course resubmittedCourse = new Course();

        when(courseService.resubmitCourse(instructorId, courseId, courseDetails)).thenReturn(resubmittedCourse);

        ResponseEntity<APIResponse<Course>> response = instructorController.resubmitCourse("Bearer token", courseId, courseDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Course resubmitted successfully", response.getBody().getMessage());
        assertEquals(resubmittedCourse, response.getBody().getData());
    }



    @Test
    void testGetInstructorById_Found() throws Exception {
        UUID instructorId = UUID.randomUUID();
        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        instructor.setFullName("Danh Handsome");

        when(instructorService.getInstructorById(instructorId)).thenReturn(instructor);

        mockMvc.perform(get("/instructors/{instructorId}", instructorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"" + instructorId.toString() + "\",\"fullName\":\"Danh Handsome\"}"));
    }

    @Test
    void testGetInstructorById_NotFound() throws Exception {
        UUID instructorId = UUID.randomUUID();

        when(instructorService.getInstructorById(instructorId)).thenReturn(null);

        mockMvc.perform(get("/instructors/{instructorId}", instructorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Instructor not found\"}"));
    }



    @Test
    void testGetInstructorsByName_Found() throws Exception {
        String name = "Danh";
        Instructor instructor1 = new Instructor();
        instructor1.setId(UUID.randomUUID());
        instructor1.setFullName("Danh Handsome");

        Instructor instructor2 = new Instructor();
        instructor2.setId(UUID.randomUUID());
        instructor2.setFullName("Danh Other");

        List<Instructor> instructors = Arrays.asList(instructor1, instructor2);

        when(instructorService.getInstructorsByName(name)).thenReturn(instructors);

        mockMvc.perform(get("/instructors/search/{instructorName}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":\"" + instructor1.getId() + "\",\"fullName\":\"Danh Handsome\"}," +
                        "{\"id\":\"" + instructor2.getId() + "\",\"fullName\":\"Danh Other\"}]"));
    }

    @Test
    void testGetInstructorsByName_NotFound() throws Exception {
        String name = "NonExistent";

        when(instructorService.getInstructorsByName(name)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/instructors/search/{instructorName}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"No instructors found with the given name\"}"));
    }

    @Test
    void testViewDashboard() {
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("Welcome Message", "Welcome John Doe");
        dashboardData.put("Total of sales", new BigDecimal("1000.00"));
        dashboardData.put("Total of courses", 5);
        dashboardData.put("Total of enroll", 50L);
        dashboardData.put("Total of students", 40L);

        when(instructorService.viewDashboard()).thenReturn(dashboardData);

        ResponseEntity<?> response = instructorController.viewDashboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dashboardData, response.getBody());

        verify(instructorService, times(1)).viewDashboard();
    }

    @Test
    void testGetTotalEarnings() {
        // Given
        BigDecimal totalEarnings = BigDecimal.valueOf(1000);
        String token = "Bearer token";
        when(jwtService.extractEmail(anyString())).thenReturn("instructor@example.com");
        when(instructorService.getInstructorIdByEmail(anyString())).thenReturn(instructorId);
        when(instructorService.getTotalEarnings(any(UUID.class))).thenReturn(totalEarnings);

        APIResponse<BigDecimal> expectedResponse = new APIResponse<>();
        expectedResponse.setData(totalEarnings);

        // When
        ResponseEntity<APIResponse<BigDecimal>> response = instructorController.getTotalEarnings(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getData(), response.getBody().getData());
    }

    @Test
    void testGetPayouts() {
        // Given
        List<Payout> payouts = Collections.singletonList(new Payout());
        String token = "Bearer token";
        when(jwtService.extractEmail(anyString())).thenReturn("instructor@example.com");
        when(instructorService.getInstructorIdByEmail(anyString())).thenReturn(instructorId);
        when(instructorService.getPayouts(any(UUID.class))).thenReturn(payouts);

        APIResponse<List<Payout>> expectedResponse = new APIResponse<>();
        expectedResponse.setData(payouts);

        // When
        ResponseEntity<APIResponse<List<Payout>>> response = instructorController.getPayouts(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getData(), response.getBody().getData());
    }

    @Test
    void testWithdraw() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100);
        String token = "Bearer token";
        doNothing().when(instructorService).withdraw(any(UUID.class), any(BigDecimal.class));

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", " Withdrawal successfully");

        // When
        ResponseEntity<?> response = instructorController.withdraw(token, amount);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.get("message"), ((Map<String, Object>) response.getBody()).get("message"));
    }
}
