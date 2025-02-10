package group_2.cursus.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class LogoutModel {
    
    @Valid
    @NotBlank(message = "token is required")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
