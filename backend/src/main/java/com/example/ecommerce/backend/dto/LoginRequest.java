package com.example.ecommerce.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

// Authentication DTOs
@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}