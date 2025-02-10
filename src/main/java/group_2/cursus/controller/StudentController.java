package group_2.cursus.controller;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.Instructor;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseCourses(@RequestBody List<Long> courseIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Assuming the email is stored in the token

        Map<String, Object> response = new HashMap<>();

        List<Long> alreadyPurchasedCourseIds = studentService.purchaseCoursesByEmail(email, courseIds);

        if (!alreadyPurchasedCourseIds.isEmpty()) {
            String message = "Student has already purchased course with ID(s): " + alreadyPurchasedCourseIds;
            response.put("message", message);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        response.put("message", "Courses purchased successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/enrolled")
    public List<Course> getEnrolledCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Assuming the email is stored in the token
        return studentService.getEnrolledCoursesByEmail(email);
    }

    @PostMapping("/courses/{courseID}/enroll")
    public ResponseEntity<Map<String, Object>> enrollCourse(@PathVariable Long courseID) {
        Map<String, Object> res = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            UUID studentID = studentService.getStudentIdByEmail(email);

            studentService.enrollCourse(studentID, courseID);
            String courseName = this.courseRepository.findCourseNameById(courseID);
            res.put("message", "STUDENT: " + email + " ENROLLED COURSE: " + courseName);

            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            res.put("message", message);
            HttpStatus status;
            switch (message) {
                case "Course not found":
                    status = HttpStatus.NOT_FOUND;
                    break;
                case "Student hasn't bought this course":
                    status = HttpStatus.FORBIDDEN;
                    break;
                case "Student already enrolled in this course":
                    status = HttpStatus.CONFLICT;
                    break;
                default:
                    status = HttpStatus.BAD_REQUEST;
            }
            return new ResponseEntity<>(res, status);
        }
    }

    @GetMapping("/subscribed")
    public ResponseEntity<Map<String, Object>> getSubscribedInstructors() {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UUID studentID = studentService.getStudentIdByEmail(username);

            List<Instructor> instructors = studentService.getSubscribedInstructors(studentID);

            if (instructors.isEmpty()) {
                response.put("message", "This student did not subscribe to any courses");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("instructors", instructors);
                return ResponseEntity.ok(response);
            }
        } catch (RuntimeException e) {
            String message = e.getMessage();
            response.put("message", message);
            HttpStatus status;
            if (message.startsWith("Student not found with ID: ")) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }
            return new ResponseEntity<>(response, status);
        }
    }
}
