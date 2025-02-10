package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Course;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private Student student1, student2;

    private Instructor instructor1, instructor2;

    private Course course1, course2;

    private int page;
    private int pageSize;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        page = 1;
        pageSize = 5;

        student1 = new Student("student1@gmail.com", "123456", "Student 1", true);
        student1.setId(UUID.randomUUID());
        student2 = new Student("student2@gmail.com", "123456", "Student 2", false);

        instructor1 = new Instructor("instructor1@gmail.com", "123456", "Instructor 1", true);
        instructor1.setId(UUID.randomUUID());
        instructor2 = new Instructor("instructor2@gmail.com", "123456", "Instructor 2", false);

        course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseName("Spring Boot");
        course1.setPrice(new BigDecimal("89.99"));
        course1.setInstructor(instructor1);

        course2 = new Course();
        course2.setCourseId(2L);
        course2.setPrice(new BigDecimal("59.99"));
        course2.setInstructor(instructor1);
    }

    @Test
    public void testViewStudentList_Success(){
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Student> studentList = Arrays.asList(student1, student2);
        Page<Student> studentPage = new PageImpl<>(studentList, pageable, 1);


        when(adminService.viewStudentList(pageable)).thenReturn(studentPage);

        Page<Student> result = adminController.viewStudentList(page, pageSize);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("student1@gmail.com", result.getContent().get(0).getEmail());
        verify(adminService, times(1)).viewStudentList(pageable);
    }

    @Test
    public void testViewInstructorList_Success(){
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Instructor> instructorList = Arrays.asList(instructor1, instructor2);
        Page<Instructor> instructorPage = new PageImpl<>(instructorList, pageable, 1);

        when(adminService.viewInstructorList(pageable)).thenReturn(instructorPage);

        Page<Instructor> result = adminController.viewInstructorList(page, pageSize);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("instructor1@gmail.com", result.getContent().get(0).getEmail());
        verify(adminService, times(1)).viewInstructorList(pageable);
    }

    @Test
    public void  testBlockOrUnblockStudent_BlockStudent_Success(){
        String status = "false";
        student1.setStatus(true);

        when(adminService.blockOrUnblockUser(student1.getId(), false)).thenReturn(student1);

        ResponseEntity<APIResponse<User>> response = adminController.blockOrUnblockStudent(student1.getId(), status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Block student successfully", response.getBody().getMessage());
        assertEquals(student1, response.getBody().getData());

        verify(adminService, times(1)).blockOrUnblockUser(student1.getId(),false);
    }

    @Test
    public void  testBlockOrUnblockStudent_UnBlockStudent_Success(){
        String status = "true";
        student1.setStatus(false);

        when(adminService.blockOrUnblockUser(student1.getId(), true)).thenReturn(student1);

        ResponseEntity<APIResponse<User>> response = adminController.blockOrUnblockStudent(student1.getId(), status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Unblock student successfully", response.getBody().getMessage());
        assertEquals(student1, response.getBody().getData());

        verify(adminService, times(1)).blockOrUnblockUser(student1.getId(),true);
    }

    @Test
    public void testBlockOrUnblockStudent_InvalidStatus(){
        UUID studentId = UUID.randomUUID();
        String status = "invalid";

        ResponseEntity<APIResponse<User>> response = adminController.blockOrUnblockStudent(studentId, status);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status is not empty and must be true or false", response.getBody().getMessage());
        verify(adminService, times(0)).blockOrUnblockUser(any(UUID.class), anyBoolean());
    }

    @Test
    public void  testBlockOrUnblockInstructor_BlockInstructor_Success(){
        String status = "false";
        instructor1.setStatus(true);

        when(adminService.blockOrUnblockUser(instructor1.getId(), false)).thenReturn(instructor1);

        ResponseEntity<APIResponse<User>> response = adminController.blockorUnblockInstructor(instructor1.getId(), status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Block instructor successfully", response.getBody().getMessage());
        assertEquals(instructor1, response.getBody().getData());

        verify(adminService, times(1)).blockOrUnblockUser(instructor1.getId(),false);
    }

    @Test
    public void  testBlockOrUnblockInstructor_UnBlockInstructor_Success(){
        String status = "true";
        instructor1.setStatus(false);

        when(adminService.blockOrUnblockUser(instructor1.getId(), true)).thenReturn(instructor1);

        ResponseEntity<APIResponse<User>> response = adminController.blockorUnblockInstructor(instructor1.getId(), status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Unblock instructor successfully", response.getBody().getMessage());
        assertEquals(instructor1, response.getBody().getData());

        verify(adminService, times(1)).blockOrUnblockUser(instructor1.getId(),true);
    }

    @Test
    public void testBlockOrUnblockInstructor_InvalidStatus(){
        UUID instructorId = UUID.randomUUID();
        String status = "invalid";

        ResponseEntity<APIResponse<User>> response = adminController.blockorUnblockInstructor(instructorId, status);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status is not empty and must be true or false", response.getBody().getMessage());
        verify(adminService, times(0)).blockOrUnblockUser(any(UUID.class), anyBoolean());
    }

    @Test
    public void testViewCourseList_Success(){
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Course> courseList = Arrays.asList(course1, course2);
        Page<Course> coursePage = new PageImpl<>(courseList, pageable, 1);


        when(adminService.viewCourseList(pageable)).thenReturn(coursePage);

        Page<Course> result = adminController.viewCourseList(page, pageSize);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(adminService, times(1)).viewCourseList(pageable);
    }

    @Test
    public void testViewDetailsACourse_Success(){
        when(adminService.viewDetailsACourse(course1.getCourseId())).thenReturn(course1);

        ResponseEntity<APIResponse<Course>> response = adminController.viewDetailsACourse(course1.getCourseId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course1, response.getBody().getData());
        assertEquals("Find course successfully!", response.getBody().getMessage());
        verify(adminService, times(1)).viewDetailsACourse(course1.getCourseId());
    }

    @Test
    public void  testApproveCourse_Success(){
        when(adminService.approveCourse(course1.getCourseId())).thenReturn(course1);

        ResponseEntity<APIResponse<Course>> response = adminController.approveCourse(course1.getCourseId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Approve course successfully.", response.getBody().getMessage());
        assertEquals(course1, response.getBody().getData());

        verify(adminService, times(1)).approveCourse(course1.getCourseId());
    }

    @Test
    public void  testRejectCourse_Success(){
        when(adminService.rejectCourse(course1.getCourseId())).thenReturn(course1);

        ResponseEntity<APIResponse<Course>> response = adminController.rejectCourse(course1.getCourseId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reject course successfully.", response.getBody().getMessage());
        assertEquals(course1, response.getBody().getData());

        verify(adminService, times(1)).rejectCourse(course1.getCourseId());
    }

    @Test
    public void testGetDashboardStatus_Success() {
        // Arrange
        when(adminService.countStudents()).thenReturn(100L);
        when(adminService.countInstructors()).thenReturn(20L);
        when(adminService.countCourses()).thenReturn(10L);
        when(adminService.calculateRevenueForCurrentMonth()).thenReturn(new BigDecimal("1000.00"));

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.getDashboardStatus();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Welcome to Dashboard", responseBody.get("Welcome Message"));
        assertEquals(100L, responseBody.get("Total of students"));
        assertEquals(20L, responseBody.get("Total of instructors"));
        assertEquals(10L, responseBody.get("Total of courses"));
        assertEquals(new BigDecimal("1000.00"), responseBody.get("This month's Revenue"));
    }

    @Test
    public void testGetDashboardStatus_NoRevenue() {
        when(adminService.countStudents()).thenReturn(100L);
        when(adminService.countInstructors()).thenReturn(20L);
        when(adminService.countCourses()).thenReturn(10L);
        when(adminService.calculateRevenueForCurrentMonth()).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = adminController.getDashboardStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Welcome to Dashboard", responseBody.get("Welcome Message"));
        assertEquals(100L, responseBody.get("Total of students"));
        assertEquals(20L, responseBody.get("Total of instructors"));
        assertEquals(10L, responseBody.get("Total of courses"));
        assertEquals(BigDecimal.ZERO, responseBody.get("This month's Revenue"));
    }
}

