package group_2.cursus.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import group_2.cursus.entity.Admin;
import group_2.cursus.entity.Instructor;
import group_2.cursus.entity.Student;
import group_2.cursus.entity.User;
import group_2.cursus.model.RegisterModel;
import group_2.cursus.model.UpdateProfileModel;
import group_2.cursus.model.IntrospectModel;
import group_2.cursus.repository.AdminRepository;
import group_2.cursus.repository.InstructorRepository;
import group_2.cursus.repository.StudentRepository;
import group_2.cursus.repository.UserRepository;
import group_2.cursus.config.IntrospectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateUser_Student() {
        RegisterModel form = new RegisterModel();
        form.setEmail("phuongit9902@gmail.com");
        form.setPassword("123456");
        form.setFullName("Nguyễn Minh Phương");
        form.setGender(true);
        form.setRole("student");

        when(userRepository.existsByEmail(form.getEmail())).thenReturn(false);

        userService.createUser(form, form.getRole());

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testInitiatePasswordReset_Success() {
        String email = "test@example.com";
        Student user = new Student();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(emailService.sendPasswordResetEmail(eq(email), anyString()))
                .thenReturn("Password reset email sent successfully");

        boolean result = userService.initiatePasswordReset(email);

        assertTrue(result);
        assertNotNull(user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiry());
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void testInitiatePasswordReset_UserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = userService.initiatePasswordReset(email);

        assertFalse(result);
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        String token = "validToken";
        String newPassword = "newPassword123";
        Student user = new Student();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Student.class))).thenReturn(user);

        boolean result = userService.resetPassword(token, newPassword);

        assertTrue(result);
        assertNull(user.getPasswordResetToken());
        assertNull(user.getPasswordResetTokenExpiry());
        assertTrue(new BCryptPasswordEncoder().matches(newPassword, user.getPassword()));
    }

    @Test
    void testResetPassword_ExpiredToken() {
        String token = "expiredToken";
        String newPassword = "newPassword123";
        Student user = new Student();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));

        boolean result = userService.resetPassword(token, newPassword);

        assertFalse(result);
        assertEquals(token, user.getPasswordResetToken());
        assertNotNull(user.getPasswordResetTokenExpiry());
        verify(userRepository, never()).save(any(Student.class));
    }

    @Test
    void testResetPassword_InvalidToken() {
        String token = "invalidToken";
        String newPassword = "newPassword123";

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        boolean result = userService.resetPassword(token, newPassword);

        assertFalse(result);
    }

    @Test
    void testChangePassword_Success() throws Exception {
        String jwtToken = "validToken";
        String currentPassword = "123456";
        String newPassword = "123456789";
        String email = "phuongit9902@gmail.com";
        User user = new Student();
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(currentPassword));

        when(jwtService.introspect(any(IntrospectModel.class))).thenReturn(new IntrospectResponse(true));
        when(jwtService.extractEmail(jwtToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        boolean result = userService.changePassword(jwtToken, currentPassword, newPassword);

        assertTrue(result);
        assertTrue(new BCryptPasswordEncoder().matches(newPassword, user.getPassword()));
    }

    @Test
    void testUpdateProfile_Student() {
        UpdateProfileModel updateProfileModel = new UpdateProfileModel("Nguyễn Minh Phương", true, "New Address", "1234567890", "/new-avatar.jpg", "New Major", null);
        Student student = new Student();
        student.setEmail("phuongit9902@gmail.com");

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_STUDENT");
        Collection<GrantedAuthority> authorities = new ArrayList<>(Arrays.asList(authority));
        Authentication auth = mock(Authentication.class);
        doReturn("phuongit9902@gmail.com").when(auth).getName();
        doReturn(authorities).when(auth).getAuthorities();

        SecurityContext securityContext = mock(SecurityContext.class);
        doReturn(auth).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("phuongit9902@gmail.com")).thenReturn(Optional.of(student));
        when(studentRepository.findByEmail("phuongit9902@gmail.com")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        UpdateProfileModel result = userService.updateProfile(updateProfileModel);

        assertEquals("Nguyễn Minh Phương", result.getFullName());
        assertEquals("New Address", result.getAddress());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("/new-avatar.jpg", result.getAvatar());
        assertEquals("New Major", result.getMajor());
    }

    @Test
    void testUpdateProfile_Admin() {
        UpdateProfileModel updateProfileModel = new UpdateProfileModel("Nguyễn Minh Phương", true, "New Address", "1234567890", "/new-avatar.jpg");
        Admin admin = new Admin();
        admin.setEmail("admin@gmail.com");

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Collection<GrantedAuthority> authorities = new ArrayList<>(Arrays.asList(authority));
        Authentication auth = mock(Authentication.class);
        doReturn("admin@gmail.com").when(auth).getName();
        doReturn(authorities).when(auth).getAuthorities();

        SecurityContext securityContext = mock(SecurityContext.class);
        doReturn(auth).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        when(adminRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        UpdateProfileModel result = userService.updateProfile(updateProfileModel);

        assertEquals("Nguyễn Minh Phương", result.getFullName());
        assertEquals("New Address", result.getAddress());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals("/new-avatar.jpg", result.getAvatar());
    }
}