package group_2.cursus.service;

import group_2.cursus.entity.*;
import group_2.cursus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseStudentRepository courseStudentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public Page<Student> viewStudentList(Pageable pageable){
        return studentRepository.findAll(pageable);
    }

    public Page<Instructor> viewInstructorList(Pageable pageable){
        return instructorRepository.findAll(pageable);
    }

    public User blockOrUnblockUser(UUID id, Boolean status){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot user with user id: " + id));
        if(status == user.isStatus() && status == true){
            throw new RuntimeException("Status is unblocking");
        } else if (status == user.isStatus() && status == false) {
            throw new RuntimeException("Status is blocking");
        }else {
            user.setStatus(status);
            return userRepository.save(user);
        }
    }

    public long countStudents(){
        return studentRepository.count();
    }

    public long countInstructors(){
        return instructorRepository.count();
    }

    public long countCourses(){
        return courseRepository.count();
    }

    public BigDecimal calculateRevenueForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        return courseStudentRepository.calRevenueCurrentMonth(start, end);
    }


    public Page<Course> viewCourseList(Pageable pageable){
        return courseRepository.findAll(pageable);
    }

    public Course viewDetailsACourse(Long courseId){
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cannot find course with id: " + courseId));
    }

    public Course approveCourse(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cannot find course with id" + courseId));
        course.setActive("APPROVED");
        return courseRepository.save(course);
    }

    public Course rejectCourse(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cannot find course with id" + courseId));
        course.setActive("REJECTED");
        return courseRepository.save(course);
    }
}
