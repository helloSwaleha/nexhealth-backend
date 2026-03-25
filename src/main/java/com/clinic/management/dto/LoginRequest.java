package com.clinic.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class LoginRequest {

    @NotBlank
    private String email; // Changed from username to email

    @NotBlank
    private String password;

    // Default constructor for Jackson
    public LoginRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}