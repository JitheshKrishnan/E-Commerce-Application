package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.UserRole;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BulkEmailRequest {
    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private UserRole userRole; // Send to all users with this role
    private List<Long> userIds; // Send to specific users
    private String templateId;
    private boolean isHtml = false;
}