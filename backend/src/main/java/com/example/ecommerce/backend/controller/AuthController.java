package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.JwtResponse;
import com.example.ecommerce.backend.dto.LoginRequest;
import com.example.ecommerce.backend.dto.RefreshTokenRequest;
import com.example.ecommerce.backend.dto.RegisterRequest;
import com.example.ecommerce.backend.dto.TokenRefreshResponse;
import com.example.ecommerce.backend.model.RefreshToken;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.security.jwt.JwtUtils;
import com.example.ecommerce.backend.security.services.UserPrincipal;
import com.example.ecommerce.backend.service.RefreshTokenService;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getId());

            return ResponseEntity.ok(new ApiResponse("Login successful", new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    userPrincipal.getId(),
                    userPrincipal.getName(),
                    userPrincipal.getEmail(),
                    roles
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Login failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            User user = userService.registerUser(
                    signUpRequest.getName(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword(),
                    signUpRequest.getAddress(),
                    signUpRequest.getPhoneNumber()
            );

            return ResponseEntity.ok(new ApiResponse("User registered successfully!", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new ApiResponse("Token refreshed successfully",
                            new TokenRefreshResponse(token, requestRefreshToken)));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(Authentication authentication) {
        System.out.println(authentication.getPrincipal());
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            Long userId = userPrincipal.getId();
            refreshTokenService.deleteByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("Log out successful!", null));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("User not authenticated", null));
    }

}