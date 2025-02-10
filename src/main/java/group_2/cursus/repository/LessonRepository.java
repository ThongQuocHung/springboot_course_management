package group_2.cursus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
}
