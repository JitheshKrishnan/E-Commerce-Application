package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole newRole;
    private UserRole oldRole;
}
