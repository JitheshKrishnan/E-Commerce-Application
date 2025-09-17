package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // Basic CRUD operations
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User updateUser(User user);
    void deleteUser(Long id);
    List<User> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);

    // Authentication related
    boolean authenticateUser(String email, String password);
    User registerUser(String name, String email, String password, String address, String phoneNumber);
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    // User management
    User activateUser(Long userId);
    User deactivateUser(Long userId);
    List<User> getUsersByRole(UserRole role);
    Page<User> getUsersByRole(UserRole role, Pageable pageable);
    List<User> getActiveUsers();

    // Search and filter
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    List<User> getUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Statistics
    Long countActiveUsers();
    Long countUsersByRole(UserRole role);

    // Validation
    boolean emailExists(String email);
    boolean isValidUser(Long userId);
}