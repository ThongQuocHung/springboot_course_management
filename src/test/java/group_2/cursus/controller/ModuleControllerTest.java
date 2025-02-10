package group_2.cursus.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import group_2.cursus.config.APIResponse;
import group_2.cursus.entity.Course;
import group_2.cursus.entity.Module;
import group_2.cursus.service.ModuleService;
import org.springframework.http.ResponseEntity;

public class ModuleControllerTest {

    @InjectMocks
    private ModuleController moduleController;

    @Mock
    private ModuleService moduleService;

    private Module module;
    private Course course;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Course 1");

        module = new Module();
        module.setModuleId(1L);
        module.setModuleName("Module 1");
        module.setCreatedAt(LocalDateTime.now());
        module.setUpdatedAt(LocalDateTime.now());
        module.setCourse(course);
    }

    @Test
    public void testGetAllModule() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Module> modulePage = new PageImpl<>(Collections.singletonList(module));

        when(moduleService.getAllModule(anyLong(), any(Pageable.class))).thenReturn(modulePage);

        ResponseEntity<APIResponse<Page<Module>>> response = moduleController.getAllModule(1L, 1, 5);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().getTotalElements());
    }

    @Test
    public void testGetModuleById() {
        when(moduleService.getModuleById(anyLong())).thenReturn(module);

        ResponseEntity<APIResponse<Module>> response = moduleController.getModuleById(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData().getModuleId());
    }

    @Test
    public void testCreateModule() {
        when(moduleService.createModule(anyLong(), any(Module.class))).thenReturn(module);

        ResponseEntity<APIResponse<Module>> response = moduleController.createModule(1L, module);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData().getModuleId());
    }

    @Test
    public void testUpdateModule() {
        module.setModuleName("Updated Module Name");
        when(moduleService.updateModule(anyLong(), any(Module.class))).thenReturn(module);

        ResponseEntity<APIResponse<Module>> response = moduleController.updateModule(1L, 1L, module);

        assertNotNull(response.getBody());
        assertEquals("Updated Module Name", response.getBody().getData().getModuleName());
    }

    @Test
    public void testDeleteModule() {
        when(moduleService.deleteModule(anyLong())).thenReturn(module);

        ResponseEntity<APIResponse<Module>> response = moduleController.deleteModule(1L, 1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getData().getModuleId());
    }
}
