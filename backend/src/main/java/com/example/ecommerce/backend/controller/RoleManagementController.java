package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.RoleChangeRequest;
import com.example.ecommerce.backend.dto.RoleChangeResponse;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.model.UserRole;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    @Autowired
    private UserService userService;

    @PostMapping("/assign/{userId}")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @Valid @RequestBody RoleChangeRequest request) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserRole oldRole = user.getRole();
            UserRole newRole = UserRole.valueOf(request.getRole().toUpperCase());
            if(newRole == oldRole) return ResponseEntity.badRequest()
                    .body(new ApiResponse("Role change denied: User is already a " + newRole, null));
            user.setRole(newRole);
            User updatedUser = userService.updateUser(user);

            return ResponseEntity.ok(new ApiResponse("Role assigned successfully",
                    new RoleChangeResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), newRole, oldRole)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to assign role: " + e.getMessage(), null));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRoles() {
        List<Map<String, String>> roles = Arrays.stream(UserRole.values())
                .map(role -> Map.of(
                        "value", role.name(),
                        "label", role.getDisplayName(),
                        "description", role.getDescription()
                ))
                .toList();

        return ResponseEntity.ok(new ApiResponse("Available roles retrieved", roles));
    }

    @PostMapping("/promote-to-seller/{userId}")
    public ResponseEntity<?> promoteToSeller(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() == UserRole.SELLER) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("User is already a seller", null));
            }

            if (user.getRole() != UserRole.CUSTOMER) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Only customers can be promoted to sellers", null));
            }

            UserRole oldRole = user.getRole();
            user.setRole(UserRole.SELLER);
            User updatedUser = userService.updateUser(user);
            RoleChangeResponse response = new RoleChangeResponse(
                    updatedUser.getId(),
                    updatedUser.getName(),
                    updatedUser.getEmail(),
                    UserRole.SELLER,
                    oldRole
            );

            return ResponseEntity.ok(new ApiResponse("User promoted to seller successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to promote user: " + e.getMessage(), null));
        }
    }

    @PostMapping("/promote-to-support/{userId}")
    public ResponseEntity<?> promoteToSupport(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() == UserRole.SUPPORT) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("User is already a support staff member", null));
            }

            if (user.getRole() != UserRole.CUSTOMER) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Only customers can be promoted to support staff members", null));
            }

            UserRole oldRole = user.getRole();
            user.setRole(UserRole.SUPPORT);
            User updatedUser = userService.updateUser(user);
            RoleChangeResponse response = new RoleChangeResponse(
                    updatedUser.getId(),
                    updatedUser.getName(),
                    updatedUser.getEmail(),
                    UserRole.SELLER,
                    oldRole
            );

            return ResponseEntity.ok(new ApiResponse("User promoted to support successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to promote user: " + e.getMessage(), null));
        }
    }

    @GetMapping("/users-by-role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);

            return ResponseEntity.ok(new ApiResponse("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get users: " + e.getMessage(), null));
        }
    }
}