package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @GetMapping
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "E-commerce Backend API",
                "version", "1.0.0"
        );

        return ResponseEntity.ok(new ApiResponse("Service is healthy", health));
    }

    @GetMapping("/ready")
    public ResponseEntity<?> readinessCheck() {
        // In real implementation, check database connectivity, external services, etc.
        Map<String, Object> readiness = Map.of(
                "status", "READY",
                "database", "connected",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(new ApiResponse("Service is ready", readiness));
    }

    @GetMapping("/live")
    public ResponseEntity<?> livenessCheck() {
        Map<String, Object> liveness = Map.of(
                "status", "ALIVE",
                "timestamp", LocalDateTime.now(),
                "uptime", "Service running"
        );

        return ResponseEntity.ok(new ApiResponse("Service is alive", liveness));
    }
}