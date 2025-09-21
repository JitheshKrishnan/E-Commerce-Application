package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AnalyticsService analyticsService;

    //TODO: Use If Using Notification
//    @Autowired
//    private NotificationService notificationService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardMetrics() {
        try {
            Map<String, Object> metrics = analyticsService.getDashboardMetrics();
            return ResponseEntity.ok(new ApiResponse("Dashboard metrics retrieved successfully", metrics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get dashboard metrics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/revenue")
    public ResponseEntity<?> getRevenueAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

            BigDecimal revenue = analyticsService.getTotalRevenue(start, end);
            Map<String, BigDecimal> categoryRevenue = analyticsService.getRevenueByCategory(start, end);

            Map<String, Object> response = Map.of(
                    "totalRevenue", revenue,
                    "categoryBreakdown", categoryRevenue,
                    "period", Map.of("start", start, "end", end)
            );

            return ResponseEntity.ok(new ApiResponse("Revenue analytics retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get revenue analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/top-products")
    public ResponseEntity<?> getTopProducts(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Object[]> topProducts = analyticsService.getTopSellingProducts(limit);
            return ResponseEntity.ok(new ApiResponse("Top products retrieved successfully", topProducts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get top products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/top-customers")
    public ResponseEntity<?> getTopCustomers(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Object[]> topCustomers = analyticsService.getTopCustomers(limit);
            return ResponseEntity.ok(new ApiResponse("Top customers retrieved successfully", topCustomers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get top customers: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/orders")
    public ResponseEntity<?> getOrderAnalytics() {
        try {
            Map<String, Long> orderCounts = analyticsService.getOrderCountByStatus();
            BigDecimal averageOrderValue = analyticsService.getAverageOrderValue();

            Map<String, Object> response = Map.of(
                    "ordersByStatus", orderCounts,
                    "averageOrderValue", averageOrderValue
            );

            return ResponseEntity.ok(new ApiResponse("Order analytics retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get order analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/users")
    public ResponseEntity<?> getUserAnalytics() {
        try {
            Long totalActiveUsers = analyticsService.getTotalActiveUsers();
            Map<String, Long> usersByRole = analyticsService.getUsersByRole();

            Map<String, Object> response = Map.of(
                    "totalActiveUsers", totalActiveUsers,
                    "usersByRole", usersByRole
            );

            return ResponseEntity.ok(new ApiResponse("User analytics retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get user analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/inventory")
    public ResponseEntity<?> getInventoryAnalytics() {
        try {
            Map<String, Long> inventoryStatus = analyticsService.getInventoryStatus();
            List<Object[]> criticalStock = analyticsService.getCriticalStockItems();

            Map<String, Object> response = Map.of(
                    "inventoryStatus", inventoryStatus,
                    "criticalStockItems", criticalStock
            );

            return ResponseEntity.ok(new ApiResponse("Inventory analytics retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get inventory analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/sales-trends")
    public ResponseEntity<?> getSalesTrends(@RequestParam(defaultValue = "30") int days) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            LocalDateTime endDate = LocalDateTime.now();

            BigDecimal totalRevenue = analyticsService.getTotalRevenue(startDate, endDate);
            Map<String, BigDecimal> categoryRevenue = analyticsService.getRevenueByCategory(startDate, endDate);

            Map<String, Object> trends = Map.of(
                    "period", days + " days",
                    "totalRevenue", totalRevenue,
                    "categoryTrends", categoryRevenue,
                    "startDate", startDate,
                    "endDate", endDate
            );

            return ResponseEntity.ok(new ApiResponse("Sales trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get sales trends: " + e.getMessage(), null));
        }
    }

    //TODO: Implement While Using Notification Service
    @PostMapping("/notifications/test-email")
    public ResponseEntity<?> testEmail(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message) {
        try {
            // In real implementation, send test email via NotificationService
            return ResponseEntity.ok(new ApiResponse("Test email sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to send test email: " + e.getMessage(), null));
        }
    }

    @PostMapping("/notifications/low-stock-alert")
    public ResponseEntity<?> triggerLowStockAlert() {
        try {
            //TODO: Use If Using Notification
//            notificationService.notifyLowStock();
            return ResponseEntity.ok(new ApiResponse("Low stock alert sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to send low stock alert: " + e.getMessage(), null));
        }
    }

    @GetMapping("/system/health")
    public ResponseEntity<?> getSystemHealth() {
        try {
            //TODO: Check Actual Functionalities Instead of Placeholders
            Map<String, Object> health = Map.of(
                    "status", "healthy",
                    "timestamp", LocalDateTime.now(),
                    "uptime", "System running normally",
                    "database", "connected",
                    "services", "all services operational"
            );

            return ResponseEntity.ok(new ApiResponse("System health check completed", health));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("System health check failed: " + e.getMessage(), null));
        }
    }

    //TODO: Implement Before Deployment
    @PostMapping("/system/maintenance/cleanup")
    public ResponseEntity<?> performCleanup() {
        try {
            // In real implementation, trigger cleanup tasks
            Map<String, Object> result = Map.of(
                    "status", "initiated",
                    "tasks", List.of("cart_cleanup", "session_cleanup", "temp_files_cleanup"),
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(new ApiResponse("Cleanup tasks initiated successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to initiate cleanup: " + e.getMessage(), null));
        }
    }

    //TODO: Implement Before Deployment
    @PostMapping("/system/backup")
    public ResponseEntity<?> initiateBackup() {
        try {
            // In real implementation, trigger system backup
            Map<String, Object> backup = Map.of(
                    "status", "initiated",
                    "type", "full_backup",
                    "timestamp", LocalDateTime.now(),
                    "estimatedDuration", "30 minutes"
            );

            return ResponseEntity.ok(new ApiResponse("System backup initiated", backup));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to initiate backup: " + e.getMessage(), null));
        }
    }

    //TODO: Implement Before Deployment
    @GetMapping("/reports/daily")
    public ResponseEntity<?> getDailyReport(@RequestParam(required = false) String date) {
        try {
            LocalDateTime reportDate = date != null ? LocalDateTime.parse(date) : LocalDateTime.now().minusDays(1);

            Map<String, Object> report = Map.of(
                    "date", reportDate.toLocalDate(),
                    "revenue", analyticsService.getTotalRevenue(reportDate, reportDate.plusDays(1)),
                    "orders", "placeholder - implement with actual service calls",
                    "newUsers", "placeholder - implement with actual service calls",
                    "topProducts", analyticsService.getTopSellingProducts(5)
            );

            return ResponseEntity.ok(new ApiResponse("Daily report generated successfully", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to generate daily report: " + e.getMessage(), null));
        }
    }
}