package group_2.cursus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group_2.cursus.config.APIResponse;
import group_2.cursus.service.ModuleService;
import jakarta.validation.Valid;
import group_2.cursus.entity.Module;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("course/{courseId}/module")
public class ModuleController {
    
    @Autowired
    private ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<Page<Module>>> getAllModule(@PathVariable long courseId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int pageSize) {
        APIResponse<Page<Module>> apiResponse = new APIResponse<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        apiResponse.setData(this.moduleService.getAllModule(courseId, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<APIResponse<Module>> getModuleById(@PathVariable long moduleId) {
        APIResponse<Module> apiResponse = new APIResponse<>();

        apiResponse.setData(this.moduleService.getModuleById(moduleId));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    @PostMapping("")
    public ResponseEntity<APIResponse<Module>> createModule(@PathVariable long courseId, @Valid @RequestBody Module module) {
        APIResponse<Module> apiResponse = new APIResponse<>();

        apiResponse.setData(this.moduleService.createModule(courseId, module));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    @PutMapping("/{moduleId}")
    public ResponseEntity<APIResponse<Module>> updateModule(@PathVariable long courseId, @PathVariable long moduleId, @Valid @RequestBody Module module) {
        APIResponse<Module> apiResponse = new APIResponse<>();

        apiResponse.setData(this.moduleService.updateModule(moduleId, module));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
    
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<APIResponse<Module>> deleteModule(@PathVariable long courseId, @PathVariable long moduleId) {
        APIResponse<Module> apiResponse = new APIResponse<>();

        apiResponse.setData(this.moduleService.deleteModule(moduleId));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }   
}
