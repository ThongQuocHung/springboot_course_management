package group_2.cursus.controller;

import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.repository.UserRepository;
import group_2.cursus.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailControllerTest {

    @InjectMocks
    private EmailController emailController;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMethodName() {
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn("Mail sent Successfully");

        String result = emailController.getMethodName();

        assertEquals("Mail sent Successfully", result);
        verify(emailService).sendEmail("thuhongkhanhtoan@gmail.com", "Hi", "Chào bạn");
    }

    @Test
    void testSendVerifyEmail_UserFound() {
        String email = "phuongit9902@gmail.com";
        User user = new User() {
            @Override
            public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return null;
            }
        };
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(emailService.sendVerificationEmail(eq(email), anyString())).thenReturn("Check your email");

        String result = emailController.sendVerifyEmail(email);

        assertEquals("Check your email", result);
        verify(emailService).sendVerificationEmail(eq(email), anyString());
    }

    @Test
    void testSendVerifyEmail_UserNotFound() {
        String email = "ạkdbakdajkadakjdadkj@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = emailController.sendVerifyEmail(email);

        assertEquals("Not found your account!!!", result);
    }

    @Test
    void testVerifyEmail_Student() {
        String email = "student@example.com";
        Student student = new Student();
        student.setActive(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(userRepository.save(any(Student.class))).thenReturn(student);

        String result = emailController.verifyEmail(email);

        assertEquals("Email verified successfully", result);
        assertTrue(student.isActive());
        verify(userRepository).save(student);
    }

    @Test
    void testVerifyEmail_NonStudent() {
        String email = "user@example.com";
        User user = new User() {
            @Override
            public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return null;
            }
        };
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = emailController.verifyEmail(email);

        assertEquals("Email verified failed", result);
    }
}