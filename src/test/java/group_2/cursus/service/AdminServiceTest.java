package group_2.cursus.service;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseStudentRepository courseStudentRepository;

    @InjectMocks
    private AdminService adminService;

    private Student student1, student2;
    private Instructor instructor1, instructor2;
    private Course course1, course2;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

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
        List<Student> studentList = Arrays.asList(student1, student2);
        Page<Student> studentPage = new PageImpl<>(studentList);
        Pageable pageable = PageRequest.of(0, 5);

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<Student> result = adminService.viewStudentList(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("student1@gmail.com", result.getContent().get(0).getEmail());
        verify(studentRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testViewInstructorList_Success(){
        List<Instructor> instructorList = Arrays.asList(instructor1, instructor2);
        Page<Instructor> instructorPage = new PageImpl<>(instructorList);
        Pageable pageable = PageRequest.of(0, 5);

        when(instructorRepository.findAll(pageable)).thenReturn(instructorPage);

        Page<Instructor> result = adminService.viewInstructorList(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("instructor1@gmail.com", result.getContent().get(0).getEmail());
        verify(instructorRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testBlockOrUnblockUser_BlockUser_Success(){
        student1.setStatus(true);

        when(userRepository.findById(student1.getId())).thenReturn(Optional.of(student1));
        when(userRepository.save(any(User.class))).thenReturn(student1);

        User result = adminService.blockOrUnblockUser(student1.getId(), false);

        assertFalse(result.isStatus());
        verify(userRepository, times(1)).findById(student1.getId());
        verify(userRepository, times(1)).save(student1);
    }

    @Test
    public void testBlockOrUnblockUser_UnBlockUser_Success(){
        student1.setStatus(false);

        when(userRepository.findById(student1.getId())).thenReturn(Optional.of(student1));
        when(userRepository.save(any(User.class))).thenReturn(student1);

        User result = adminService.blockOrUnblockUser(student1.getId(), true);

        assertTrue(result.isStatus());
        verify(userRepository, times(1)).findById(student1.getId());
        verify(userRepository, times(1)).save(student1);
    }

    @Test
    public void testBlockOrUnblockUser_UserNotFound(){
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.blockOrUnblockUser(userId, true);
        });

        assertEquals("Cannot user with user id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testBlockOrUnblockUser_StatusIsSameUnBlocking(){
        student1.setStatus(true);

        when(userRepository.findById(student1.getId())).thenReturn(Optional.of(student1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.blockOrUnblockUser(student1.getId(), true);
        });

        assertEquals("Status is unblocking", exception.getMessage());
        verify(userRepository, times(1)).findById(student1.getId());
        verify(userRepository, times(0)).save(student1);
    }

    @Test
    public void testBlockOrUnblockUser_StatusIsSameBlocking(){
        student1.setStatus(false);

        when(userRepository.findById(student1.getId())).thenReturn(Optional.of(student1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.blockOrUnblockUser(student1.getId(), false);
        });

        assertEquals("Status is blocking", exception.getMessage());
        verify(userRepository, times(1)).findById(student1.getId());
        verify(userRepository, times(0)).save(student1);
    }

    @Test
    public void testViewCourseList_Success(){
        List<Course> courseList = Arrays.asList(course1, course2);
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 5);

        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        Page<Course> result = adminService.viewCourseList(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(courseRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testViewDetailsACourse_Success(){
        when(courseRepository.findById(course1.getCourseId())).thenReturn(Optional.of(course1));

        Course course = adminService.viewDetailsACourse(course1.getCourseId());

        assertNotNull(course);
        assertEquals(course1.getCourseId(), course.getCourseId());
        assertEquals(course1.getCourseName(), course.getCourseName());
        verify(courseRepository, times(1)).findById(course1.getCourseId());
    }

    @Test
    public void testViewDetailsACourse_CourseNotFound(){
        Long courseId = 3L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.viewDetailsACourse(courseId);
        });

        assertEquals("Cannot find course with id: " + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    public void testApproveCourse_Success(){
        when(courseRepository.findById(course1.getCourseId())).thenReturn(Optional.of(course1));
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        Course result = adminService.approveCourse(course1.getCourseId());

        assertEquals("APPROVED", course1.getIsActive());
        verify(courseRepository, times(1)).findById(course1.getCourseId());
        verify(courseRepository, times(1)).save(course1);
    }

    @Test
    public void testApproveCourse_CourseNotFound(){
        Long courseId = 3L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.approveCourse(courseId);
        });

        assertEquals("Cannot find course with id" + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    @Test
    public void testRejectCourse_Success(){
        when(courseRepository.findById(course1.getCourseId())).thenReturn(Optional.of(course1));
        when(courseRepository.save(any(Course.class))).thenReturn(course1);

        Course result = adminService.rejectCourse(course1.getCourseId());

        assertEquals("REJECTED", course1.getIsActive());
        verify(courseRepository, times(1)).findById(course1.getCourseId());
        verify(courseRepository, times(1)).save(course1);
    }

    @Test
    public void testRejectCourse_CourseNotFound(){
        Long courseId = 3L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.rejectCourse(courseId);
        });

        assertEquals("Cannot find course with id" + courseId, exception.getMessage());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    @Test
    public void testCountStudents() {
        when(studentRepository.count()).thenReturn(5L);

        long result = adminService.countStudents();

        assertEquals(5L, result);
        verify(studentRepository, times(1)).count();
    }

    @Test
    public void testCountInstructors() {
        when(instructorRepository.count()).thenReturn(3L);

        long result = adminService.countInstructors();

        assertEquals(3L, result);
        verify(instructorRepository, times(1)).count();
    }

    @Test
    public void testCountCourses() {
        when(courseRepository.count()).thenReturn(10L);

        long result = adminService.countCourses();

        assertEquals(10L, result);
        verify(courseRepository, times(1)).count();
    }

    @Test
    public void testCalculateRevenueForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal expectedRevenue = new BigDecimal("1000.00");

        when(courseStudentRepository.calRevenueCurrentMonth(start, end)).thenReturn(expectedRevenue);

        BigDecimal result = adminService.calculateRevenueForCurrentMonth();

        assertEquals(expectedRevenue, result);
        verify(courseStudentRepository, times(1)).calRevenueCurrentMonth(start, end);
    }
}