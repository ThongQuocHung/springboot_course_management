package group_2.cursus.service;

import group_2.cursus.entity.*;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.CourseStudentRepository;
import group_2.cursus.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseStudentRepository courseStudentRepository;

    @Autowired
    private PayoutService payoutService;

    public List<Long> purchaseCoursesByEmail(String email, List<Long> courseIds) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        UUID studentId = student.getId();
        List<Long> alreadyPurchasedCourseIds = new ArrayList<>();
        List<Long> notFoundCourseIds = new ArrayList<>();

        for (Long courseId : courseIds) {
            if (!courseRepository.existsById(courseId)) {
                notFoundCourseIds.add(courseId);
                continue;
            }

            if (courseStudentRepository.existsByCourseCourseIdAndStudentId(courseId, studentId)) {
                alreadyPurchasedCourseIds.add(courseId);
                continue;
            }

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID " + courseId));

            CourseStudentId courseStudentId = new CourseStudentId(studentId, courseId);
            CourseStudent courseStudent = new CourseStudent();
            courseStudent.setId(courseStudentId);
            courseStudent.setCourse(course);
            courseStudent.setStudent(student);
            courseStudent.setPaid(true);
            courseStudent.setPurchasedAt(LocalDateTime.now());

            courseStudentRepository.save(courseStudent);

            // Create a payout record
            BigDecimal coursePrice = course.getPrice();
            Instructor instructor = course.getInstructor();
            payoutService.createPayout(instructor, coursePrice);
        }

        if (!notFoundCourseIds.isEmpty()) {
            throw new RuntimeException("Course(s) not found with ID(s): " + notFoundCourseIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
        }

        return alreadyPurchasedCourseIds;
    }

    public List<Course> getEnrolledCoursesByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        UUID studentId = student.getId();

        return courseRepository.findEnrolledCoursesByStudentId(studentId);
    }

    public CourseStudent enrollCourse(UUID studentID, Long courseID) {
        Course course = courseRepository.findById(courseID)
                .orElseThrow(() -> new RuntimeException("Course not found with ID " + courseID));

        CourseStudentId courseStudentId = new CourseStudentId(studentID, courseID);

        CourseStudent courseStudent = courseStudentRepository.findById(courseStudentId)
                .orElseThrow(() -> new RuntimeException("Student hasn't bought this course"));

        if (!courseStudent.isPaid()) {
            throw new RuntimeException("Student hasn't bought this course");
        }
        if (courseStudent.getEnrolledAt() != null) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        courseStudent.setEnrolledAt(LocalDateTime.now());
        return courseStudentRepository.save(courseStudent);
    }

    public List<Instructor> getSubscribedInstructors(UUID studentID) {
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentID));
        List<CourseStudent> courseStudents = courseStudentRepository.findByStudent(student);

        return courseStudents.stream()
                .map(courseStudent -> courseStudent.getCourse().getInstructor())
                .distinct()
                .collect(Collectors.toList());
    }

    public UUID getStudentIdByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getId();
    }
}
