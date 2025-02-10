package group_2.cursus.repository;

import group_2.cursus.entity.CourseReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseReportRepository extends JpaRepository<CourseReport, Long> {
}