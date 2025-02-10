package group_2.cursus.repository;

import group_2.cursus.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.CourseStudent;
import group_2.cursus.entity.CourseStudentId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, CourseStudentId> {
    @Query("SELECT SUM(c.price) FROM CourseStudent cs JOIN cs.course c WHERE cs.purchasedAt BETWEEN :startDate AND :endDate")
    BigDecimal calRevenueCurrentMonth(LocalDateTime startDate, LocalDateTime endDate);

    List<CourseStudent> findByStudent(Student student);

    @Query("SELECT SUM(cs.course.price) FROM CourseStudent cs WHERE cs.course.courseId = :courseId AND cs.purchasedAt IS NOT NULL")
    BigDecimal sumCourseRevenueByCourseId(@Param("courseId") Long courseId);

    boolean existsByCourseCourseIdAndStudentId(Long courseId, UUID studentId);

}
