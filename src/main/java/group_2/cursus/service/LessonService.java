package group_2.cursus.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group_2.cursus.entity.Lesson;
import group_2.cursus.entity.Module;
import group_2.cursus.repository.LessonRepository;
import group_2.cursus.repository.ModuleRepository;

@Service
public class LessonService {
    
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    public LessonService(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }
    
    public Page<Lesson> getAllLesson(Long moduleId, Pageable pageable) {
        try {
            return this.lessonRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all lesson: " + e.getMessage());
        }
    }

    public Lesson getLessonById(Long id) {
        return this.lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Failed to get lesson by id: " + id));
    }

    public Lesson createLesson(Long moduleId, Lesson lesson) {
        try {
            Module m = this.moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new RuntimeException("Module not fount with id: " + moduleId));
            
            lesson.setModule(m);
            return this.lessonRepository.save(lesson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create lesson: " + e.getMessage());
        }
    }

    public Lesson updateLesson(Long id, Lesson lesson) {
        try {
            Lesson existingLesson = this.lessonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
            existingLesson.setLessonName(lesson.getLessonName());
            existingLesson.setDescription(lesson.getDescription());
            existingLesson.setUrlVideo(lesson.getUrlVideo());
            existingLesson.setCreatedAt(lesson.getCreatedAt());
            existingLesson.setUpdatedAt(LocalDateTime.now());
           
            return this.lessonRepository.save(existingLesson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update lesson: " + e.getMessage());
        }
    }

    public Lesson deleteLesson(Long id) {
        try {
            Lesson lesson = this.lessonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

            this.lessonRepository.delete(lesson);
            return lesson;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete lesson: " + e.getMessage());
        }
    }
}
