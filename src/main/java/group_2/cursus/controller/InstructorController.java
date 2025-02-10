package group_2.cursus.controller;

import java.math.BigDecimal;
import java.util.*;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.CourseReview;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Payout;
import group_2.cursus.repository.InstructorRepository;
import group_2.cursus.service.InstructorService;
import group_2.cursus.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import group_2.cursus.entity.Course;
import group_2.cursus.service.CourseService;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private InstructorRepository instructorRepository;


    @GetMapping("/dashboard")
    public ResponseEntity<?> viewDashboard(){
        Map<String, Object> data = instructorService.viewDashboard();

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @Autowired
    private JwtService jwtService;

    private UUID extractInstructorIdFromToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtService.extractEmail(jwtToken);
        System.out.println("Extracted email: " + email);  // Logging email
        return instructorService.getInstructorIdByEmail(email);
    }

    @PostMapping("/courses")
    public ResponseEntity<APIResponse<Course>> createCourse(@RequestHeader(value = "Authorization", required = false) String token,
                                                            @Valid @RequestBody Course course) {
        APIResponse<Course> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        if (courseService.courseExists(course.getCourseName())) {
            response.setMessage("Course name already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        Course newCourse = courseService.createCourse(instructorId, course);
        response.setData(newCourse);
        response.setMessage("Course created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<APIResponse<Page<Course>>> getCoursesByInstructor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        APIResponse<Page<Course>> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Course> courses = courseService.getCoursesByInstructor(instructorId, pageable);
        response.setData(courses);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<APIResponse<Course>> getCourseById(@RequestHeader(value = "Authorization", required = false) String token,
                                                             @PathVariable Long courseId) {
        APIResponse<Course> response = new APIResponse<>();
            UUID instructorId = extractInstructorIdFromToken(token);
            Course course = courseService.getCourseByIdAndInstructor(instructorId, courseId);
            response.setData(course);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<APIResponse<Course>> updateCourse(@RequestHeader(value = "Authorization", required = false) String token,
                                                            @PathVariable Long courseId, @Valid @RequestBody Course courseDetails) {
        APIResponse<Course> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        Course updatedCourse = courseService.updateCourse(instructorId, courseId, courseDetails);
        response.setData(updatedCourse);
        response.setMessage("Course updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long courseId) {
        APIResponse<Course> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        courseService.deleteCourse(instructorId, courseId);
        response.setMessage("Course deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PutMapping("/courses/resubmit/{courseId}")
    public ResponseEntity<APIResponse<Course>> resubmitCourse(@RequestHeader(value = "Authorization", required = false) String token,
                                                              @PathVariable Long courseId, @RequestBody Course courseDetails) {
        APIResponse<Course> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        Course resubmittedCourse = courseService.resubmitCourse(instructorId, courseId, courseDetails);
        response.setData(resubmittedCourse);
        response.setMessage("Course resubmitted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<?> getInstructorById(@PathVariable UUID instructorId) {
        Instructor instructor = instructorService.getInstructorById(instructorId);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Instructor not found"));
        }
        return new ResponseEntity<>(instructor, HttpStatus.OK);
    }

    @GetMapping("/search/{instructorName}")
    public ResponseEntity<?> getInstructorsByName(@PathVariable String instructorName) {
        List<Instructor> instructors = instructorService.getInstructorsByName(instructorName);
        if (instructors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "No instructors found with the given name"));
        }
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }

    // Update by Minh Phương at 10.20 PM 11/07/2024
    @GetMapping("/instructor/reviews")
    public ResponseEntity<APIResponse<List<CourseReview>>> getInstructorReviews(Authentication authentication) {
        APIResponse<List<CourseReview>> apiResponse = new APIResponse<>();
        try {
            String username = authentication.getName();
            Instructor instructor = instructorRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));

            List<CourseReview> reviews = courseService.getInstructorReviews(instructor.getId());
            apiResponse.setData(reviews);
            apiResponse.setMessage("Instructor reviews retrieved successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            apiResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @GetMapping("/earnings")
    public ResponseEntity<APIResponse<BigDecimal>> getTotalEarnings(@RequestHeader(value = "Authorization", required = false) String token) {
        APIResponse<BigDecimal> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        BigDecimal totalEarnings = instructorService.getTotalEarnings(instructorId);
        response.setData(totalEarnings);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/payouts")
    public ResponseEntity<APIResponse<List<Payout>>> getPayouts(@RequestHeader(value = "Authorization", required = false) String token) {
        APIResponse<List<Payout>> response = new APIResponse<>();
        UUID instructorId = extractInstructorIdFromToken(token);
        List<Payout> payouts = instructorService.getPayouts(instructorId);
        response.setData(payouts);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestBody BigDecimal amount) {
        UUID instructorId = extractInstructorIdFromToken(token);
        instructorService.withdraw(instructorId, amount);
        Map<String, Object> response = new HashMap<>();
        response.put("message", " Withdrawal successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
