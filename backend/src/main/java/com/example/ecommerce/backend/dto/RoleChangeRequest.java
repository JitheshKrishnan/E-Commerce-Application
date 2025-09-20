package com.example.ecommerce.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class RoleChangeRequest {
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "(?i)CUSTOMER|SELLER|SUPPORT|ADMIN", message = "Invalid role")
    private String role;

    @NotBlank(message = "Reason is required")
    private String reason; // Optional reason for role change
}