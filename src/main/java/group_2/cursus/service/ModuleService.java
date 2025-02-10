package group_2.cursus.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.Module;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.ModuleRepository;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private CourseRepository courseRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }
    
    public Page<Module> getAllModule(Long courseId, Pageable pageable) {
        try {
            return this.moduleRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all module: " + e.getMessage());
        }
    }

    public Module getModuleById(Long id) {
        return this.moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Failed to get module by id: " + id));
    }

    public Module createModule(Long courseId, Module module) {
        try {
            Course c = this.courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
            
            module.setCourse(c);
            return this.moduleRepository.save(module);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create module: " + e.getMessage());
        }
    }

    public Module updateModule(Long id, Module module) {
        try {
            Module existingModule = this.moduleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
            existingModule.setModuleName(module.getModuleName());
            existingModule.setCreatedAt(module.getCreatedAt());
            existingModule.setUpdatedAt(LocalDateTime.now());
           
            return this.moduleRepository.save(existingModule);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update module: " + e.getMessage());
        }
    }

    public Module deleteModule(Long id) {
        try {
            Module module = this.moduleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

            this.moduleRepository.delete(module);
            return module;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete module: " + e.getMessage());
        }
    }
}
