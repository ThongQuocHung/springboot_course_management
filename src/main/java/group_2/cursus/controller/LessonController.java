package group_2.cursus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Lesson;
import group_2.cursus.service.LessonService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/module/{moduleId}/lesson")
public class LessonController {
    
    @Autowired
    private LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<Page<Lesson>>> getAllLesson(@PathVariable long moduleId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int pageSize) {
        APIResponse<Page<Lesson>> apiResponse = new APIResponse<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        apiResponse.setData(this.lessonService.getAllLesson(moduleId, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<APIResponse<Lesson>> getLessonById(@PathVariable long lessonId) {
        APIResponse<Lesson> apiResponse = new APIResponse<>();

        apiResponse.setData(this.lessonService.getLessonById(lessonId));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("")
    public ResponseEntity<APIResponse<Lesson>> createLesson(@PathVariable long moduleId, @Valid @RequestBody Lesson lesson) {
        APIResponse<Lesson> apiResponse = new APIResponse<>();

        apiResponse.setData(this.lessonService.createLesson(moduleId, lesson));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    @PutMapping("/{lessonId}")
    public ResponseEntity<APIResponse<Lesson>> updateLesson(@PathVariable long moduleId, @PathVariable long lessonId, @Valid @RequestBody Lesson lesson) {
        APIResponse<Lesson> apiResponse = new APIResponse<>();

        apiResponse.setData(this.lessonService.updateLesson(lessonId, lesson));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<APIResponse<Lesson>> deleteLesson(@PathVariable long moduleId, @PathVariable long lessonId) {
        APIResponse<Lesson> apiResponse = new APIResponse<>();

        apiResponse.setData(this.lessonService.deleteLesson(lessonId));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }   
}
