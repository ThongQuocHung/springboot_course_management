package group_2.cursus.controller;

import group_2.cursus.model.RegisterModel;
import group_2.cursus.model.UpdateProfileModel;
import group_2.cursus.config.APIResponse;
import group_2.cursus.config.AuthenticationResponse;
import group_2.cursus.model.LoginModel;
import group_2.cursus.model.LogoutModel;
import group_2.cursus.service.JwtService;
import group_2.cursus.service.UserService;
import jakarta.validation.Valid;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.nimbusds.jose.JOSEException;

@Controller
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<APIResponse<RegisterModel>> registerUser(@Valid @RequestBody RegisterModel form) {
        APIResponse<RegisterModel> apiResponse = new APIResponse<>();

        userService.createUser(form, form.getRole());
        apiResponse.setData(form);
        apiResponse.setMessage("User registered successfully! Redirecting to login page...");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginModel loginModel) {
        APIResponse<AuthenticationResponse> apiResponse = new APIResponse<>();

        apiResponse.setMessage("Login Successfully!");
        apiResponse.setData(this.jwtService.login(loginModel));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Void>> logout(@Valid @RequestBody LogoutModel logoutModel)
            throws JOSEException, ParseException {
        APIResponse<Void> apiResponse = new APIResponse<>();

        this.jwtService.logout(logoutModel);
        apiResponse.setMessage("Logout Successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<String>> forgotPassword(@RequestBody @Valid Map<String, String> forgotPasswordRequest) {
        APIResponse<String> apiResponse = new APIResponse<>();
        String email = forgotPasswordRequest.get("email");

        if (email == null || email.isEmpty()) {
            apiResponse.setMessage("Email is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        try {
            boolean result = userService.initiatePasswordReset(email);
            if (result) {
                apiResponse.setMessage("Password reset instructions sent to your email.");
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setMessage("User not found with the provided email.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
        } catch (Exception e) {
            apiResponse.setMessage("Error initiating password reset: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(
            @RequestParam String token,
            @RequestBody @Valid Map<String, String> resetPasswordRequest) {
        APIResponse<String> apiResponse = new APIResponse<>();

        String newPassword = resetPasswordRequest.get("newPassword");
        String confirmPassword = resetPasswordRequest.get("confirmPassword");

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            apiResponse.setMessage("New password and confirm password are required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        if (!newPassword.equals(confirmPassword)) {
            apiResponse.setMessage("New password and confirm password do not match.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        try {
            boolean result = userService.resetPassword(token, newPassword);
            if (result) {
                apiResponse.setMessage("Password reset successful.");
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setMessage("Failed to reset password. Token may be invalid or expired.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
        } catch (Exception e) {
            apiResponse.setMessage("Error resetting password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<APIResponse<String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> passwordMap) throws JOSEException, ParseException {
        APIResponse<String> apiResponse = new APIResponse<>();

        String currentPassword = passwordMap.get("currentPassword");
        String newPassword = passwordMap.get("newPassword");

        String token = authHeader.substring(7);
        boolean result = userService.changePassword(token, currentPassword, newPassword);
        if (result) {
            apiResponse.setMessage("Password changed successfully.");
            apiResponse.setData("Success");
            return ResponseEntity.ok(apiResponse);
        } else {
            apiResponse.setMessage("Failed to change password. Please check your current password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }


    @PostMapping("/update-profile")
    public ResponseEntity<APIResponse<UpdateProfileModel>> UpdateProfileModel(
            @Valid @RequestBody UpdateProfileModel updateProfileModel) {
        APIResponse<UpdateProfileModel> apiResponse = new APIResponse<>();

        apiResponse.setData(this.userService.updateProfile(updateProfileModel));
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

}
