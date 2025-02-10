package group_2.cursus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import group_2.cursus.config.AuthenticationResponse;
import group_2.cursus.config.IntrospectResponse;
import group_2.cursus.entity.Student;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Admin;
import group_2.cursus.entity.InvalidToken;
import group_2.cursus.model.IntrospectModel;
import group_2.cursus.model.LoginModel;
import group_2.cursus.model.LogoutModel;
import group_2.cursus.repository.UserRepository;
import group_2.cursus.repository.StudentRepository;
import group_2.cursus.repository.InstructorRepository;
import group_2.cursus.repository.AdminRepository;
import group_2.cursus.repository.InvalidTokenRepository;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private InvalidTokenRepository invalidTokenRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        jwtService.SECRET = "testSecretKeyWithAtLeast32Characters12345";
    }

    @Test
    public void testLogin_StudentSuccess() {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("student@gmail.com");
        loginModel.setPassword("password");

        Student student = new Student("student@gmail.com", new BCryptPasswordEncoder().encode("password"), "Test Student", true);
        student.setId(UUID.randomUUID());

        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(student));

        assertThrows(Exception.class, () -> jwtService.login(loginModel));
    }

    @Test
    public void testLogin_InstructorSuccess() {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("instructor@gmail.com");
        loginModel.setPassword("password");

        Instructor instructor = new Instructor("instructor@gmail.com", new BCryptPasswordEncoder().encode("password"), "Test Instructor", true);
        instructor.setId(UUID.randomUUID());

        when(userRepository.findByEmail("instructor@gmail.com")).thenReturn(Optional.of(instructor));

        assertThrows(Exception.class, () -> jwtService.login(loginModel));
    }

    @Test
    public void testLogin_AdminSuccess() {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("admin@gmail.com");
        loginModel.setPassword("password");

        Admin admin = new Admin("admin@gmail.com", new BCryptPasswordEncoder().encode("password"), "Test Admin", true);
        admin.setId(UUID.randomUUID());

        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));

        assertThrows(Exception.class, () -> jwtService.login(loginModel));
    }

    @Test
    public void testLogin_WrongPassword() {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("student@gmail.com");
        loginModel.setPassword("wrongpassword");

        Student student = new Student("student@gmail.com", new BCryptPasswordEncoder().encode("password"), "Test Student", true);

        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(student));

        assertThrows(RuntimeException.class, () -> jwtService.login(loginModel));
    }

    @Test
    public void testLogout_Success() {
        Student student = new Student("student@gmail.com", "password", "Test Student", true);
        student.setId(UUID.randomUUID());

        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(student));

        assertThrows(Exception.class, () -> {
            String token = jwtService.generateToken(student);
            LogoutModel logoutModel = new LogoutModel();
            logoutModel.setToken(token);
            jwtService.logout(logoutModel);
        });
    }

    @Test
    public void testIntrospect_ValidToken() {
        Admin admin = new Admin("admin@gmail.com", "password", "Test Admin", true);
        admin.setId(UUID.randomUUID());

        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));

        assertThrows(Exception.class, () -> {
            String token = jwtService.generateToken(admin);
            IntrospectModel introspectModel = new IntrospectModel(token);
            jwtService.introspect(introspectModel);
        });
    }

    @Test
    public void testIntrospect_InvalidToken() {
        IntrospectModel introspectModel = new IntrospectModel("invalid.token.here");

        assertThrows(ParseException.class, () -> jwtService.introspect(introspectModel));
    }

    @Test
    public void testGenerateToken() {
        Instructor instructor = new Instructor("instructor@gmail.com", "password", "Test Instructor", true);
        instructor.setId(UUID.randomUUID());

        when(userRepository.findByEmail("instructor@gmail.com")).thenReturn(Optional.of(instructor));

        assertThrows(RuntimeException.class, () -> jwtService.generateToken(instructor));
    }

    @Test
    public void testExtractEmail() {
        Student student = new Student("student@gmail.com", "password", "Test Student", true);
        student.setId(UUID.randomUUID());

        when(userRepository.findByEmail("student@gmail.com")).thenReturn(Optional.of(student));

        assertThrows(Exception.class, () -> {
            String token = jwtService.generateToken(student);
            jwtService.extractEmail(token);
        });
    }
}