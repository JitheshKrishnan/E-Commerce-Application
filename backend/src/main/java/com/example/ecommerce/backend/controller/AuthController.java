package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.LoginRequest;
import com.example.ecommerce.backend.dto.LoginResponse;
import com.example.ecommerce.backend.dto.RegisterRequest;
import com.example.ecommerce.backend.dto.UserResponse;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getAddress(),
                    request.getPhoneNumber()
            );

            UserResponse response = new UserResponse(user);
            return ResponseEntity.ok(new ApiResponse("User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Registration failed: " + e.getMessage(), null));
        }
    }

    //TODO: JWT Token Implementation To Be Done
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            boolean isValid = userService.authenticateUser(request.getEmail(), request.getPassword());

            if (isValid) {
                User user = userService.getUserByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // In real implementation, generate JWT token here
                String token = "jwt-token-" + user.getId(); // Placeholder

                LoginResponse response = new LoginResponse(token, new UserResponse(user));
                return ResponseEntity.ok(new ApiResponse("Login successful", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Invalid credentials", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Login failed: " + e.getMessage(), null));
        }
    }

    //TODO: Invalidate JWT Token
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In real implementation, invalidate JWT token
        return ResponseEntity.ok(new ApiResponse("Logout successful", null));
    }

    //TODO: Incomplete
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            // Implementation for password reset
            return ResponseEntity.ok(new ApiResponse("Password reset email sent", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Password reset failed: " + e.getMessage(), null));
        }
    }
}