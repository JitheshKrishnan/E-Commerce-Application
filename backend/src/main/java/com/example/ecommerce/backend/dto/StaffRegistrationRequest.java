package com.example.ecommerce.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class StaffRegistrationRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String address;
    private String phoneNumber;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "SUPPORT|ADMIN", message = "Role must be SUPPORT or ADMIN")
    private String role;

    @NotBlank(message = "Department is required")
    private String department;

    private String employeeId;
}