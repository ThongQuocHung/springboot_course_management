package group_2.cursus.repository;

import group_2.cursus.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourse_CourseId(Long courseId);

    @Query("SELECT cr FROM CourseReview cr WHERE cr.course.instructor.id = :instructorId")
    List<CourseReview> findByInstructorId(@Param("instructorId") UUID instructorId);
}