package group_2.cursus.repository;

import group_2.cursus.entity.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT i FROM Course i WHERE LOWER(i.courseName) LIKE LOWER(CONCAT('%', :courseName, '%'))")
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    @Query("SELECT c FROM Course c WHERE c.subCategory.subCategoryId = :subCategoryId")
    List<Course> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

    @Query("SELECT c FROM Course c JOIN c.courseStudents cs JOIN cs.student s WHERE s.id = :studentId")
    List<Course> findEnrolledCoursesByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT COUNT(cs) " +
            "FROM Course c LEFT JOIN c.courseStudents cs " +
            "WHERE c.instructor.id = :instructorId AND cs.isPaid = true")
    Long totalStudentByInstructorId(@Param("instructorId") UUID instructorId);

    @Query("SELECT COUNT(cs) " +
            "FROM Course c LEFT JOIN c.courseStudents cs " +
            "WHERE c.instructor.id = :instructorId AND cs.enrolledAt IS NOT NULL")
    Long totalEnrollByInstructorId(@Param("instructorId") UUID instructorId);

    @Query("SELECT SUM(c.price) " +
            "FROM Course c JOIN c.courseStudents cs " +
            "WHERE c.instructor.id = :instructorId AND cs.isPaid = true")
    BigDecimal totalSaleByInstructorId(@Param("instructorId") UUID instructorId);

    Optional<Course> findByCourseName(String courseName);

    @Query("SELECT c.courseName FROM Course c WHERE c.courseId = :courseId")
    String findCourseNameById(Long courseId);

    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") UUID instructorId);

    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :courseName, '%'))")
    Page<Course> findByCourseNameContainingIgnoreCase(@Param("courseName") String courseName, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.subCategory.subCategoryId = :subCategoryId")
    Page<Course> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN c.courseStudents cs JOIN cs.student s WHERE s.id = :studentId")
    Page<Course> findEnrolledCoursesByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

    Page<Course> findByInstructor(Instructor instructor, Pageable pageable);
}
