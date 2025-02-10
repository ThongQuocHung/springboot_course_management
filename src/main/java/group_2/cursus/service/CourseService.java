package group_2.cursus.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import group_2.cursus.entity.*;
import group_2.cursus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @Autowired
    private CourseReportRepository courseReportRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    public boolean courseExists(String courseName) {
        return courseRepository.findByCourseName(courseName).isPresent();
    }

    public Course createCourse(UUID instructorId, Course course) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Cannot find instructor with id: " + instructorId));
        course.setInstructor(instructor);
        if (course.getSubCategory() != null && course.getSubCategory().getSubCategoryId() > 0) {
            SubCategory subCategory = subCategoryRepository.findById(course.getSubCategory().getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Cannot find subCategory with id: " + course.getSubCategory().getSubCategoryId()));
            course.setSubCategory(subCategory);
        }
        return courseRepository.save(course);
    }

    public Page<Course> getCoursesByInstructor(UUID instructorId, Pageable pageable) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Cannot find instructor with id: " + instructorId));
        return courseRepository.findByInstructor(instructor, pageable);
    }

    public Course getCourseByIdAndInstructor(UUID  instructorId, Long courseId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Cannot find instructor with id: " + instructorId));
        return instructor.getCourses().stream()
                .filter(course -> course.getCourseId() == courseId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find course with id: " + courseId + " for instructor with id: " + instructorId));
    }

    public Course updateCourse(UUID instructorId, Long courseId, Course courseDetails) {
        Course course = getCourseByIdAndInstructor(instructorId, courseId);

        course.setCourseName(courseDetails.getCourseName());
        course.setDescription(courseDetails.getDescription());
        course.setPrice(courseDetails.getPrice());
        course.setThumb(courseDetails.getThumb());
        course.setView(courseDetails.getView());
        course.setStatus(courseDetails.isStatus());
        course.setUpdatedAt(LocalDateTime.now());
        course.setSubCategory(courseDetails.getSubCategory());

        return courseRepository.save(course);
    }

    public void deleteCourse(UUID instructorId, Long courseId) {
        Course course = getCourseByIdAndInstructor(instructorId, courseId);
        courseRepository.delete(course);
    }

    public Course resubmitCourse(UUID instructorId, Long courseId, Course courseDetails) {
        Course course = getCourseByIdAndInstructor(instructorId, courseId);

        if (course.getIsActive().equals("APPROVED")) {
            throw new RuntimeException("Course is not rejected and cannot be resubmitted.");
        }

        course.setCourseName(courseDetails.getCourseName());
        course.setDescription(courseDetails.getDescription());
        course.setPrice(courseDetails.getPrice());
        course.setThumb(courseDetails.getThumb());
        course.setView(courseDetails.getView());
        course.setActive("PENDING");
        course.setStatus(courseDetails.isStatus());
        course.setUpdatedAt(LocalDateTime.now());
        course.setSubCategory(courseDetails.getSubCategory());

        return courseRepository.save(course);
    }

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Page<Course> searchCoursesByName(String courseName, Pageable pageable) {
        return courseRepository.findByCourseNameContainingIgnoreCase(courseName, pageable);
    }

    public Page<Course> getCoursesBySubCategory(Long categoryId, Pageable pageable) {
        return courseRepository.findBySubCategoryId(categoryId, pageable);
    }

    public Page<Course> getEnrolledCourses(Student student, Pageable pageable) {
        return courseRepository.findEnrolledCoursesByStudentId(student.getId(), pageable);
    }
    
    public CourseReview addCourseReview(Long courseId, UUID studentId, int rating, String comment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return courseReviewRepository.save(review);
    }

    public List<CourseReview> getCourseReviews(Long courseId) {
        return courseReviewRepository.findByCourse_CourseId(courseId);
    }

    public List<CourseReview> getInstructorReviews(UUID instructorId) {
        return courseReviewRepository.findByInstructorId(instructorId);
    }

    public CourseReport reportCourse(Long courseId, UUID studentId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseReport report = new CourseReport();
        report.setCourse(course);
        report.setStudent(student);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());

        return courseReportRepository.save(report);
    }
}

