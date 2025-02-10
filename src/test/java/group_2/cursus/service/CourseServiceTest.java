package group_2.cursus.service;

import group_2.cursus.entity.*;
import group_2.cursus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseReviewRepository courseReviewRepository;

    @Mock
    private CourseReportRepository courseReportRepository;

    @Mock
    private InstructorRepository instructorRepository;

    private UUID instructorId;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCourseReview() {
        Long courseId = 1L;
        UUID studentId = UUID.randomUUID();
        int rating = 5;
        String comment = "Khóa học tuyệt vời!";
        Course course = new Course();
        Student student = new Student();
        CourseReview review = new CourseReview();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseReviewRepository.save(any(CourseReview.class))).thenReturn(review);

        CourseReview result = courseService.addCourseReview(courseId, studentId, rating, comment);

        assertNotNull(result);
        verify(courseReviewRepository).save(any(CourseReview.class));
    }

    @Test
    void testGetCourseReviews() {
        Long courseId = 1L;
        List<CourseReview> reviews = Arrays.asList(new CourseReview(), new CourseReview());

        when(courseReviewRepository.findByCourse_CourseId(courseId)).thenReturn(reviews);

        List<CourseReview> result = courseService.getCourseReviews(courseId);

        assertEquals(2, result.size());
    }

    @Test
    void testGetInstructorReviews() {
        UUID instructorId = UUID.randomUUID();
        List<CourseReview> reviews = Arrays.asList(new CourseReview(), new CourseReview());

        when(courseReviewRepository.findByInstructorId(instructorId)).thenReturn(reviews);

        List<CourseReview> result = courseService.getInstructorReviews(instructorId);

        assertEquals(2, result.size());
    }

    @Test
    void testReportCourse() {
        Long courseId = 1L;
        UUID studentId = UUID.randomUUID();
        String reason = "Dỡ !!!";
        Course course = new Course();
        Student student = new Student();
        CourseReport report = new CourseReport();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseReportRepository.save(any(CourseReport.class))).thenReturn(report);

        CourseReport result = courseService.reportCourse(courseId, studentId, reason);

        assertNotNull(result);
        verify(courseReportRepository).save(any(CourseReport.class));
    }

    @Test
    void testCourseExists() {
        String courseName = "Existing Course";
        when(courseRepository.findByCourseName(courseName)).thenReturn(Optional.of(new Course()));

        boolean result = courseService.courseExists(courseName);

        assertTrue(result);
        verify(courseRepository, times(1)).findByCourseName(courseName);
    }

    @Test
    void testCourseDoesNotExist() {
        String courseName = "Non-Existing Course";
        when(courseRepository.findByCourseName(courseName)).thenReturn(Optional.empty());

        boolean result = courseService.courseExists(courseName);

        assertFalse(result);
        verify(courseRepository, times(1)).findByCourseName(courseName);
    }

    @Test
    void testCreateCourse() {
        UUID instructorId = UUID.randomUUID();
        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>());
        Course course = new Course();
        course.setInstructor(instructor);

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.createCourse(instructorId, course);

        assertEquals(course, result);
        verify(instructorRepository, times(1)).findById(instructorId);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testCreateCourseInstructorNotFound() {
        UUID instructorId = UUID.randomUUID();
        Course course = new Course();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(instructorId, course);
        });

        assertEquals("Cannot find instructor with id: " + instructorId, exception.getMessage());
        verify(instructorRepository, times(1)).findById(instructorId);
        verify(courseRepository, times(0)).save(course);
    }

    @Test
    void testGetCoursesByInstructor() {
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Course> page = new PageImpl<>(courseList, pageable, courseList.size());
        UUID instructorId = UUID.randomUUID();

        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>(courseList));

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructor(any(Instructor.class), eq(pageable))).thenReturn(page);

        Page<Course> result = courseService.getCoursesByInstructor(instructorId, pageable);

        assertEquals(courseList, result.getContent());
        verify(instructorRepository, times(1)).findById(instructorId);
        verify(courseRepository, times(1)).findByInstructor(any(Instructor.class), eq(pageable));
    }

    @Test
    void testGetCoursesByInstructorNotFound() {
        UUID instructorId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.getCoursesByInstructor(instructorId, pageable);
        });

        assertEquals("Cannot find instructor with id: " + instructorId, exception.getMessage());
        verify(instructorRepository, times(1)).findById(instructorId);
    }

    @Test
    void testGetCourseByIdAndInstructor() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Instructor instructor = new Instructor();
        Course course = new Course();
        course.setCourseId(courseId);
        instructor.setCourses(new HashSet<>(Collections.singletonList(course)));

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        Course result = courseService.getCourseByIdAndInstructor(instructorId, courseId);

        assertEquals(course, result);
        verify(instructorRepository, times(1)).findById(instructorId);
    }

    @Test
    void testGetCourseByIdAndInstructorNotFound() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.getCourseByIdAndInstructor(instructorId, courseId);
        });

        assertEquals("Cannot find instructor with id: " + instructorId, exception.getMessage());
        verify(instructorRepository, times(1)).findById(instructorId);
    }

    @Test
    void testUpdateCourse() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Course existingCourse = new Course();
        existingCourse.setCourseId(courseId);
        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>(Collections.singleton(existingCourse)));
        Course courseDetails = new Course();
        courseDetails.setCourseName("Updated Name");

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);

        Course result = courseService.updateCourse(instructorId, courseId, courseDetails);

        assertEquals(existingCourse, result);
        assertEquals("Updated Name", existingCourse.getCourseName());
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    void testUpdateCourseInstructorNotFound() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Course courseDetails = new Course();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.updateCourse(instructorId, courseId, courseDetails);
        });

        assertEquals("Cannot find instructor with id: " + instructorId, exception.getMessage());
        verify(instructorRepository, times(1)).findById(instructorId);
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    @Test
    void testDeleteCourse() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Course course = new Course();
        course.setCourseId(courseId);
        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>(Collections.singleton(course)));

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        courseService.deleteCourse(instructorId, courseId);

        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void testDeleteCourseInstructorNotFound() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.deleteCourse(instructorId, courseId);
        });

        assertEquals("Cannot find instructor with id: " + instructorId, exception.getMessage());
        verify(instructorRepository, times(1)).findById(instructorId);
        verify(courseRepository, times(0)).delete(any(Course.class));
    }

    @Test
    void testResubmitCourse() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Course existingCourse = new Course();
        existingCourse.setCourseId(courseId);
        existingCourse.setActive("REJECTED");
        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>(Collections.singleton(existingCourse)));
        Course courseDetails = new Course();
        courseDetails.setCourseName("Resubmitted Name");

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);

        Course result = courseService.resubmitCourse(instructorId, courseId, courseDetails);

        assertEquals(existingCourse, result);
        assertEquals("Resubmitted Name", existingCourse.getCourseName());
        assertEquals("PENDING", existingCourse.getIsActive());
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    void testResubmitCourseNotRejected() {
        UUID instructorId = UUID.randomUUID();
        Long courseId = 1L;
        Course existingCourse = new Course();
        existingCourse.setCourseId(courseId);
        existingCourse.setActive("APPROVED");
        Instructor instructor = new Instructor();
        instructor.setCourses(new HashSet<>(Collections.singleton(existingCourse)));
        Course courseDetails = new Course();

        when(instructorRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.resubmitCourse(instructorId, courseId, courseDetails);
        });

        assertEquals("Course is not rejected and cannot be resubmitted.", exception.getMessage());
        verify(courseRepository, times(0)).save(existingCourse);
    }

    @Test
    void testGetAllCourses() {
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        Page<Course> result = courseService.getAllCourses(pageable);

        assertEquals(2, result.getContent().size());
        verify(courseRepository, times(1)).findAll(pageable);
    }


    @Test
    void testSearchCoursesByName() {
        String courseName = "Java";
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findByCourseNameContainingIgnoreCase(courseName, pageable)).thenReturn(coursePage);

        Page<Course> result = courseService.searchCoursesByName(courseName, pageable);

        assertEquals(2, result.getContent().size());
        verify(courseRepository, times(1)).findByCourseNameContainingIgnoreCase(courseName, pageable);
    }

    @Test
    void testGetCoursesBySubCategory() {
        Long subcategoryId = 1L;
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findBySubCategoryId(subcategoryId, pageable)).thenReturn(coursePage);

        Page<Course> result = courseService.getCoursesBySubCategory(subcategoryId, pageable);

        assertEquals(2, result.getContent().size());
        verify(courseRepository, times(1)).findBySubCategoryId(subcategoryId, pageable);
    }

    @Test
    void testGetEnrolledCourses() {
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        student.setId(studentId);
        List<Course> courseList = Arrays.asList(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findEnrolledCoursesByStudentId(studentId, pageable)).thenReturn(coursePage);

        Page<Course> result = courseService.getEnrolledCourses(student, pageable);

        assertEquals(2, result.getContent().size());
        verify(courseRepository, times(1)).findEnrolledCoursesByStudentId(studentId, pageable);
    }
}