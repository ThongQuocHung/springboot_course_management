package group_2.cursus.controller;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.*;
import group_2.cursus.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/students")
    Page<Student> viewStudentList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return adminService.viewStudentList(pageable);
    }

    @GetMapping("/instructors")
    Page<Instructor> viewInstructorList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return adminService.viewInstructorList(pageable);
    }

    @PostMapping("/students/block/{id}")
    ResponseEntity<APIResponse<User>> blockOrUnblockStudent(@PathVariable UUID id, @RequestBody String status) {
        APIResponse<User> apiResponse = new APIResponse<>();
        if (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false")) {
            boolean sta = Boolean.parseBoolean(status);
            if (!sta) {
                apiResponse.setMessage("Block student successfully");
            } else {
                apiResponse.setMessage("Unblock student successfully");
            }
            User user = adminService.blockOrUnblockUser(id, sta);
            apiResponse.setData(user);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } else {
            apiResponse.setMessage("Status is not empty and must be true or false");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PostMapping("/instructors/block/{id}")
    ResponseEntity<APIResponse<User>> blockorUnblockInstructor(@PathVariable UUID id, @RequestBody String status) {
        APIResponse<User> apiResponse = new APIResponse<>();
        if (status.equalsIgnoreCase("true") || status.equalsIgnoreCase("false")) {
            boolean sta = Boolean.parseBoolean(status);
            if (!sta) {
                apiResponse.setMessage("Block instructor successfully");
            } else {
                apiResponse.setMessage("Unblock instructor successfully");
            }
            User user = adminService.blockOrUnblockUser(id, sta);
            apiResponse.setData(user);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } else {
            apiResponse.setMessage("Status is not empty and must be true or false");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("Welcome Message", "Welcome to Dashboard");
        status.put("Total of students", adminService.countStudents());
        status.put("Total of instructors", adminService.countInstructors());
        status.put("Total of courses", adminService.countCourses());

        BigDecimal thisMonthRevenue = Optional.ofNullable(adminService.calculateRevenueForCurrentMonth())
                .orElse(BigDecimal.ZERO);
        status.put("This month's Revenue", thisMonthRevenue);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/courses")
    Page<Course> viewCourseList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return adminService.viewCourseList(pageable);
    }

    @GetMapping("/courses/{id}")
    ResponseEntity<APIResponse<Course>> viewDetailsACourse(@PathVariable Long id) {
        APIResponse<Course> apiResponse = new APIResponse<>();

        Course course = adminService.viewDetailsACourse(id);
        apiResponse.setData(course);
        apiResponse.setMessage("Find course successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/courses/approve/{id}")
    ResponseEntity<APIResponse<Course>> approveCourse(@PathVariable Long id) {
        APIResponse<Course> apiResponse = new APIResponse<>();
        Course course = adminService.approveCourse(id);

        apiResponse.setData(course);
        apiResponse.setMessage("Approve course successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/courses/reject/{id}")
    ResponseEntity<APIResponse<Course>> rejectCourse(@PathVariable Long id) {
        APIResponse<Course> apiResponse = new APIResponse<>();
        Course course = adminService.rejectCourse(id);
        
        apiResponse.setData(course);
        apiResponse.setMessage("Reject course successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
