package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Course;
import group_2.cursus.entity.CourseReport;
import group_2.cursus.entity.CourseReview;
import group_2.cursus.entity.Student;
import group_2.cursus.repository.StudentRepository;
import group_2.cursus.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<APIResponse<Page<Course>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = courseService.getAllCourses(pageable);
        APIResponse<Page<Course>> apiResponse = new APIResponse<>();
        apiResponse.setData(coursesPage);
        apiResponse.setMessage("Courses retrieved successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/search/{courseName}")
    public ResponseEntity<APIResponse<Page<Course>>> searchCourses(
            @PathVariable String courseName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = courseService.searchCoursesByName(courseName, pageable);
        APIResponse<Page<Course>> apiResponse = new APIResponse<>();
        apiResponse.setData(coursesPage);
        if (coursesPage.isEmpty()) {
            apiResponse.setMessage("No courses found with the given name");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
        apiResponse.setMessage("Courses found successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/category/{subcategoryId}")
    public ResponseEntity<APIResponse<Page<Course>>> getCoursesByCategory(
            @PathVariable Long subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = courseService.getCoursesBySubCategory(subcategoryId, pageable);
        APIResponse<Page<Course>> apiResponse = new APIResponse<>();
        apiResponse.setData(coursesPage);
        apiResponse.setMessage("Courses for category retrieved successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/enrolled")
    public ResponseEntity<APIResponse<Page<Course>>> getEnrolledCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursesPage = courseService.getEnrolledCourses(student, pageable);
        APIResponse<Page<Course>> apiResponse = new APIResponse<>();
        apiResponse.setData(coursesPage);
        apiResponse.setMessage("Enrolled courses for student: " + student.getEmail());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<APIResponse<CourseReview>> addReview(@PathVariable Long courseId, @RequestParam int rating,
            @RequestParam String comment) {
        APIResponse<CourseReview> apiResponse = new APIResponse<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseReview review = courseService.addCourseReview(courseId, student.getId(), rating, comment);

        apiResponse.setData(review);
        apiResponse.setMessage("Review added successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<APIResponse<List<CourseReview>>> getReviews(@PathVariable Long courseId) {
        APIResponse<List<CourseReview>> apiResponse = new APIResponse<>();
        List<CourseReview> reviews = courseService.getCourseReviews(courseId);
        apiResponse.setData(reviews);
        apiResponse.setMessage("Reviews retrieved successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{courseId}/report")
    public ResponseEntity<APIResponse<CourseReport>> reportCourse(@PathVariable Long courseId,
            @RequestParam String reason) {
        APIResponse<CourseReport> apiResponse = new APIResponse<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CourseReport report = courseService.reportCourse(
                courseId, student.getId(), reason);

        apiResponse.setData(report);
        apiResponse.setMessage("Course reported successfully");
        return ResponseEntity.ok(apiResponse);
    }
}
