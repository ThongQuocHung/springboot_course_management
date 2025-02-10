package group_2.cursus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import group_2.cursus.entity.Course;
import group_2.cursus.entity.Module;
import group_2.cursus.repository.CourseRepository;
import group_2.cursus.repository.ModuleRepository;

public class ModuleServiceTest {

    @InjectMocks
    private ModuleService moduleService;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;

    private Module module;
    private Course course;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        module = new Module();
        module.setModuleId(1L);
        module.setModuleName("Module 1");

        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Course 1");
        course.setDescription("Description");
        course.setPrice(new BigDecimal(2000));
        course.setThumb("/");

        module.setCourse(course);
    }

     @Test
    public void testGetAllModule_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Module> modulePage = new PageImpl<>(Set.of(module).stream().collect(Collectors.toList()));

        when(moduleRepository.findAll(pageable)).thenReturn(modulePage);

        Page<Module> result = moduleService.getAllModule(course.getCourseId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(moduleRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetAllModule_Failure() {
        Pageable pageable = PageRequest.of(0, 10);
        when(moduleRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moduleService.getAllModule(course.getCourseId(), pageable);
        });

        assertEquals("Failed to get all module: Database error", exception.getMessage());
        verify(moduleRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetModuleById() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));

        Module result = moduleService.getModuleById(module.getModuleId());

        assertNotNull(result);
        assertEquals(module.getModuleId(), result.getModuleId());
        verify(moduleRepository, times(1)).findById(module.getModuleId());
    }

   @Test
   public void testCreateModule() {
       when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
       when(moduleRepository.save(any(Module.class))).thenReturn(module);

       Module result = moduleService.createModule(course.getCourseId(), module);

       assertNotNull(result);
       assertEquals(module.getModuleId(), result.getModuleId());
       verify(courseRepository, times(1)).findById(course.getCourseId());
       verify(moduleRepository, times(1)).save(module);
   }

    @Test
    public void testUpdateModule() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        module.setModuleName("Updated Module Name");
        Module result = moduleService.updateModule(module.getModuleId(), module);

        assertNotNull(result);
        assertEquals("Updated Module Name", result.getModuleName());
        verify(moduleRepository, times(1)).findById(module.getModuleId());
        verify(moduleRepository, times(1)).save(module);
    }

    @Test
    public void testDeleteModule() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));

        Module result = moduleService.deleteModule(module.getModuleId());

        assertNotNull(result);
        assertEquals(module.getModuleId(), result.getModuleId());
        verify(moduleRepository, times(1)).findById(module.getModuleId());
        verify(moduleRepository, times(1)).delete(module);
    }

    @Test
    public void testGetModuleById_NotFound() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moduleService.getModuleById(1L);
        });

        assertEquals("Failed to get module by id: 1", exception.getMessage());
        verify(moduleRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateModule_NotFound() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moduleService.updateModule(1L, module);
        });

        assertEquals("Failed to update module: Module not found with id: 1", exception.getMessage());
        verify(moduleRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteModule_NotFound() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moduleService.deleteModule(1L);
        });

        assertEquals("Failed to delete module: Module not found with id: 1", exception.getMessage());
        verify(moduleRepository, times(1)).findById(1L);
    }
}
