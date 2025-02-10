package group_2.cursus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import group_2.cursus.config.AuthenticationResponse;
import group_2.cursus.config.CustomJwtDecoder;
import group_2.cursus.config.IntrospectResponse;
import group_2.cursus.model.*;
import group_2.cursus.service.JwtService;
import group_2.cursus.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomJwtDecoder customJwtDecoder;

    @BeforeEach
    void setUp() throws ParseException, JOSEException {
        when(customJwtDecoder.decode(anyString())).thenReturn(
                Jwt.withTokenValue("token")
                        .header("alg", "none")
                        .claim("sub", "user")
                        .build()
        );

        when(jwtService.introspect(any(IntrospectModel.class))).thenReturn(new IntrospectResponse(true));
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        RegisterModel form = new RegisterModel();
        form.setEmail("phuongit9902@gmail.com");
        form.setPassword("123456");
        form.setFullName("Test User");
        form.setGender(true);
        form.setRole("student");

        doNothing().when(userService).createUser(any(RegisterModel.class), anyString());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully! Redirecting to login page..."))
                .andExpect(jsonPath("$.data.email").value("phuongit9902@gmail.com"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginModel loginModel = new LoginModel();
        loginModel.setEmail("phuongit9902@gmail.com");
        loginModel.setPassword("123456");

        AuthenticationResponse authResponse = new AuthenticationResponse("minhphuong123");
        when(jwtService.login(any(LoginModel.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login Successfully!"))
                .andExpect(jsonPath("$.data.token").value("minhphuong123"));
    }

    @Test
    void testLogout_Success() throws Exception {
        LogoutModel logoutModel = new LogoutModel();
        logoutModel.setToken("minhphuong123");

        doNothing().when(jwtService).logout(any(LogoutModel.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout Successfully!"));
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "test@example.com");

        when(userService.initiatePasswordReset("test@example.com")).thenReturn(true);

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset instructions sent to your email."));
    }

    @Test
    void testForgotPassword_UserNotFound() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "nonexistent@example.com");

        when(userService.initiatePasswordReset("nonexistent@example.com")).thenReturn(false);

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with the provided email."));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newPassword", "newPassword123");
        requestBody.put("confirmPassword", "newPassword123");

        when(userService.resetPassword("validToken", "newPassword123")).thenReturn(true);

        mockMvc.perform(post("/auth/reset-password?token=validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful."));
    }

    @Test
    void testResetPassword_PasswordMismatch() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newPassword", "newPassword123");
        requestBody.put("confirmPassword", "differentPassword");

        mockMvc.perform(post("/auth/reset-password?token=validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("New password and confirm password do not match."));
    }

    @Test
    void testResetPassword_InvalidToken() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newPassword", "newPassword123");
        requestBody.put("confirmPassword", "newPassword123");

        when(userService.resetPassword("invalidToken", "newPassword123")).thenReturn(false);

        mockMvc.perform(post("/auth/reset-password?token=invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to reset password. Token may be invalid or expired."));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        String currentPassword = "123456";
        String newPassword = "123456789";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("currentPassword", currentPassword);
        passwordMap.put("newPassword", newPassword);

        when(userService.changePassword(anyString(), eq(currentPassword), eq(newPassword))).thenReturn(true);

        mockMvc.perform(post("/auth/change-password")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully."))
                .andExpect(jsonPath("$.data").value("Success"));
    }

    @Test
    void testChangePassword_Failure_WrongCurrentPassword() throws Exception {
        String currentPassword = "wrongPassword";
        String newPassword = "123456789";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("currentPassword", currentPassword);
        passwordMap.put("newPassword", newPassword);

        when(userService.changePassword(anyString(), eq(currentPassword), eq(newPassword))).thenReturn(false);

        mockMvc.perform(post("/auth/change-password")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMap)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to change password. Please check your current password."));
    }

    @Test
    void testChangePassword_Failure_InvalidToken() throws Exception {
        String currentPassword = "123456";
        String newPassword = "123456789";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("currentPassword", currentPassword);
        passwordMap.put("newPassword", newPassword);

        when(userService.changePassword(anyString(), eq(currentPassword), eq(newPassword)))
                .thenThrow(new JOSEException("Invalid JWT token"));

        mockMvc.perform(post("/auth/change-password")
                        .header("Authorization", "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMap)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Invalid JWT token"));
    }

    @Test
    void testChangePassword_Failure_UserNotFound() throws Exception {
        String currentPassword = "123456";
        String newPassword = "123456789";
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("currentPassword", currentPassword);
        passwordMap.put("newPassword", newPassword);

        when(userService.changePassword(anyString(), eq(currentPassword), eq(newPassword)))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/auth/change-password")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMap)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
    @Test
    void testUpdateProfile_Success() throws Exception {
        UpdateProfileModel updateProfileModel = new UpdateProfileModel("Minh Phuong New", true, "New Address", "1234567890", "/new-avatar.jpg", "New Major", "New About");
        when(userService.updateProfile(any(UpdateProfileModel.class))).thenReturn(updateProfileModel);

        mockMvc.perform(post("/auth/update-profile")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Minh Phuong New"))
                .andExpect(jsonPath("$.data.address").value("New Address"));
    }
}