package group_2.cursus.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterModel {

    @Valid
    @NotBlank(message = "email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email is not valid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be greater than or equal to 6 characters")
    private String password;

    @NotBlank(message = "name is required")
    @Size(min = 4, max = 200, message = "name must be between 4 and 200 characters")
    private String fullName;

    private boolean gender;

    private String role = "student";

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
