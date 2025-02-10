package group_2.cursus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import group_2.cursus.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.CourseStudentRepository;
import group_2.cursus.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseStudentRepository courseStudentRepository;

    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private Course course1;
    private Course course2;

    @BeforeEach
    public void setup() {
        student = new Student();
        student.setId(UUID.fromString("91f01576-cbb2-41a0-b354-2af04f8d3752"));
        student.setEmail("npb.danhg@gmail.com");

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
    }

    @Test
    public void testGetStudentIdByEmail_StudentNotFound() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.getStudentIdByEmail("npb.danhg@gmail.com"));

        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    public void testPurchaseCourses_Success() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.existsById(2L)).thenReturn(true);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course2));
        when(courseStudentRepository.existsByCourseCourseIdAndStudentId(1L, student.getId())).thenReturn(false);
        when(courseStudentRepository.existsByCourseCourseIdAndStudentId(2L, student.getId())).thenReturn(false);

        List<Long> result = studentService.purchaseCoursesByEmail("npb.danhg@gmail.com", Arrays.asList(1L, 2L));

        assertTrue(result.isEmpty());
        verify(courseStudentRepository, times(2)).save(any(CourseStudent.class));
        verify(payoutService, times(2)).createPayout(any(Instructor.class), any(BigDecimal.class));
    }

    @Test
    public void testPurchaseCourses_StudentNotFound() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.purchaseCoursesByEmail("npb.danhg@gmail.com", Arrays.asList(1L, 2L)));

        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    public void testPurchaseCourses_CourseNotFound() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));
        when(courseRepository.existsById(1L)).thenReturn(false);
        when(courseRepository.existsById(2L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.purchaseCoursesByEmail("npb.danhg@gmail.com", List.of(1L, 2L)));

        assertEquals("Course(s) not found with ID(s): 1, 2", exception.getMessage());
    }

    @Test
    public void testPurchaseCourses_AlreadyPurchased() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(courseStudentRepository.existsByCourseCourseIdAndStudentId(1L, student.getId())).thenReturn(true);

        List<Long> result = studentService.purchaseCoursesByEmail("npb.danhg@gmail.com", List.of(1L));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));

        // Verify that save(...) was never called on courseStudentRepository
        verify(courseStudentRepository, never()).save(any(CourseStudent.class));
        verify(payoutService, never()).createPayout(any(Instructor.class), any(BigDecimal.class));
    }

    @Test
    public void testEnrollCourse_Success() {
        UUID studentId = student.getId();
        CourseStudentId courseStudentId = new CourseStudentId(studentId, 1L);
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setId(courseStudentId);
        courseStudent.setCourse(course1);
        courseStudent.setStudent(student);
        courseStudent.setPaid(true);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseStudentRepository.findById(courseStudentId)).thenReturn(Optional.of(courseStudent));
        when(courseStudentRepository.save(any(CourseStudent.class))).thenAnswer(invocation -> {
            CourseStudent cs = invocation.getArgument(0);
            cs.setEnrolledAt(LocalDateTime.now());
            return cs;
        });

        CourseStudent enrolledCourseStudent = studentService.enrollCourse(studentId, 1L);

        assertNotNull(enrolledCourseStudent.getEnrolledAt());
        verify(courseStudentRepository).save(any(CourseStudent.class));
    }


    @Test
    public void testEnrollCourse_CourseNotFound() {
        UUID studentId = student.getId();
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.enrollCourse(studentId, 1L));

        assertEquals("Course not found with ID 1", exception.getMessage());
    }


    @Test
    public void testEnrollCourse_StudentHasNotBoughtCourse() {
        UUID studentId = student.getId();
        CourseStudentId courseStudentId = new CourseStudentId(studentId, 1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseStudentRepository.findById(courseStudentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.enrollCourse(studentId, 1L));

        assertEquals("Student hasn't bought this course", exception.getMessage());
    }


    @Test
    public void testEnrollCourse_AlreadyEnrolled() {
        UUID studentId = student.getId();
        CourseStudentId courseStudentId = new CourseStudentId(studentId, 1L);
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setId(courseStudentId);
        courseStudent.setCourse(course1);
        courseStudent.setStudent(student);
        courseStudent.setPaid(true);
        courseStudent.setEnrolledAt(LocalDateTime.now());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(courseStudentRepository.findById(courseStudentId)).thenReturn(Optional.of(courseStudent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.enrollCourse(studentId, 1L));

        assertEquals("Student is already enrolled in this course", exception.getMessage());
    }


    @Test
    public void testGetSubscribedInstructors_Success() {
        UUID studentId = student.getId();
        Instructor instructor1 = new Instructor();
        instructor1.setId(UUID.randomUUID());

        Course course1 = new Course();
        course1.setInstructor(instructor1);

        CourseStudent courseStudent1 = new CourseStudent();
        courseStudent1.setCourse(course1);
        courseStudent1.setStudent(student);

        List<CourseStudent> courseStudents = List.of(courseStudent1);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseStudentRepository.findByStudent(student)).thenReturn(courseStudents);

        List<Instructor> instructors = studentService.getSubscribedInstructors(studentId);

        assertEquals(1, instructors.size());
        assertEquals(instructor1.getId(), instructors.get(0).getId());
    }


    @Test
    public void testGetSubscribedInstructors_StudentNotFound() {
        UUID studentId = student.getId();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.getSubscribedInstructors(studentId));

        assertEquals("Student not found with ID: " + studentId, exception.getMessage());
    }


    @Test
    public void testGetSubscribedInstructors_NoSubscriptions() {
        UUID studentId = student.getId();
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseStudentRepository.findByStudent(student)).thenReturn(List.of());

        List<Instructor> instructors = studentService.getSubscribedInstructors(studentId);

        assertTrue(instructors.isEmpty());
    }

    @Test
    public void testGetStudentIdByEmail_Success() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));

        UUID result = studentService.getStudentIdByEmail("npb.danhg@gmail.com");

        assertEquals(student.getId(), result);
    }

    @Test
    public void testGetEnrolledCoursesByEmail_Success() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));
        when(courseRepository.findEnrolledCoursesByStudentId(student.getId())).thenReturn(Arrays.asList(course1, course2));

        List<Course> courses = studentService.getEnrolledCoursesByEmail("npb.danhg@gmail.com");

        assertNotNull(courses);
        assertEquals(2, courses.size());
        assertTrue(courses.contains(course1));
        assertTrue(courses.contains(course2));

        verify(studentRepository, times(1)).findByEmail("npb.danhg@gmail.com");
        verify(courseRepository, times(1)).findEnrolledCoursesByStudentId(student.getId());
    }

    @Test
    public void testGetEnrolledCoursesByEmail_StudentNotFound() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.getEnrolledCoursesByEmail("npb.danhg@gmail.com"));

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository, times(1)).findByEmail("npb.danhg@gmail.com");
        verify(courseRepository, times(0)).findEnrolledCoursesByStudentId(any(UUID.class));
    }

    @Test
    public void testGetEnrolledCoursesByEmail_NoEnrolledCourses() {
        when(studentRepository.findByEmail("npb.danhg@gmail.com")).thenReturn(Optional.of(student));
        when(courseRepository.findEnrolledCoursesByStudentId(student.getId())).thenReturn(List.of());

        List<Course> courses = studentService.getEnrolledCoursesByEmail("npb.danhg@gmail.com");

        assertNotNull(courses);
        assertTrue(courses.isEmpty());

        verify(studentRepository, times(1)).findByEmail("npb.danhg@gmail.com");
        verify(courseRepository, times(1)).findEnrolledCoursesByStudentId(student.getId());
    }

}
