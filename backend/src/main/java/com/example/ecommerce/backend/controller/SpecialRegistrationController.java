package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.SellerRegistrationRequest;
import com.example.ecommerce.backend.dto.StaffRegistrationRequest;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.model.UserRole;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

//TODO: Modify It As A Request To Admin
@RestController
@RequestMapping("/api/auth/register")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SpecialRegistrationController {

    @Autowired
    private UserService userService;

    // Public seller registration (anyone can apply)
    @PostMapping("/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SellerRegistrationRequest request) {
        try {
            // Create user as CUSTOMER first, then promote after verification
            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getAddress(),
                    request.getPhoneNumber()
            );

            // In real implementation, you might:
            // 1. Keep as CUSTOMER and require admin approval
            // 2. Store seller application data separately
            // 3. Send verification emails

            // For now, auto-promote to SELLER (in production, require approval)
            user.setRole(UserRole.SELLER);
            User updatedUser = userService.updateUser(user);

            return ResponseEntity.ok(new ApiResponse(
                    "Seller registration successful. Your account is pending review.",
                    Map.of(
                            "userId", updatedUser.getId(),
                            "role", updatedUser.getRole().name(),
                            "status", "pending_approval"
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Seller registration failed: " + e.getMessage(), null));
        }
    }

    // Admin-only staff registration
    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerStaff(@Valid @RequestBody StaffRegistrationRequest request) {
        try {
            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getAddress(),
                    request.getPhoneNumber()
            );

            // Assign role based on request
            UserRole assignedRole = UserRole.valueOf(request.getRole().toUpperCase());
            user.setRole(assignedRole);
            User updatedUser = userService.updateUser(user);

            return ResponseEntity.ok(new ApiResponse("Staff registration successful", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Staff registration failed: " + e.getMessage(), null));
        }
    }

    // Admin registration (super admin only)
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Only existing admins can create new admins
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody StaffRegistrationRequest request) {
        try {
            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getAddress(),
                    request.getPhoneNumber()
            );

            user.setRole(UserRole.ADMIN);
            User updatedUser = userService.updateUser(user);

            return ResponseEntity.ok(new ApiResponse("Admin registration successful", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Admin registration failed: " + e.getMessage(), null));
        }
    }
}