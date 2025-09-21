package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.ChangePasswordRequest;
import com.example.ecommerce.backend.dto.UpdateProfileRequest;
import com.example.ecommerce.backend.dto.UserResponse;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600) //TODO: For Production, Modify Origins
public class UserController {

    private final UserService userService;

    // Get current user profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse response = new UserResponse(user);
            return ResponseEntity.ok(new ApiResponse("Profile retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get profile: " + e.getMessage(), null));
        }
    }

    // Update current user profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setName(request.getName());
            if(request.getAddress() != null) user.setAddress(request.getAddress());
            if(request.getPhoneNumber() != null)user.setPhoneNumber(request.getPhoneNumber());

            User updatedUser = userService.updateUser(user);
            UserResponse response = new UserResponse(updatedUser);

            return ResponseEntity.ok(new ApiResponse("Profile updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update profile: " + e.getMessage(), null));
        }
    }

    // Change password
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean success = userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());

            if (success) {
                return ResponseEntity.ok(new ApiResponse("Password changed successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Invalid old password", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to change password: " + e.getMessage(), null));
        }
    }

    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.getAllUsers(pageable);

            Page<UserResponse> response = users.map(UserResponse::new);
            return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get users: " + e.getMessage(), null));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    public ResponseEntity<?> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.searchUsers(searchTerm, pageable);

            Page<UserResponse> response = users.map(UserResponse::new);
            return ResponseEntity.ok(new ApiResponse("Search completed", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Search failed: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable Long userId) {
        try {
            User user = userService.activateUser(userId);
            UserResponse response = new UserResponse(user);
            return ResponseEntity.ok(new ApiResponse("User activated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to activate user: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        try {
            User user = userService.deactivateUser(userId);
            UserResponse response = new UserResponse(user);
            return ResponseEntity.ok(new ApiResponse("User deactivated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to deactivate user: " + e.getMessage(), null));
        }
    }
}