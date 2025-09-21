package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.OrderResponse;
import com.example.ecommerce.backend.dto.OrderSummaryResponse;
import com.example.ecommerce.backend.dto.UserResponse;
import com.example.ecommerce.backend.model.Order;
import com.example.ecommerce.backend.model.OrderStatus;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.model.UserRole;
import com.example.ecommerce.backend.service.OrderService;
import com.example.ecommerce.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/support")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
public class SupportController {

    private final UserService userService;
    private final OrderService orderService;

    //TODO: Incomplete, Implement Before Deployment
    @GetMapping("/dashboard")
    public ResponseEntity<?> getSupportDashboard() {
        try {
            Map<String, Object> dashboard = Map.of(
                    "pendingOrders", orderService.countOrdersByStatus(OrderStatus.PENDING),
                    "processingOrders", orderService.countOrdersByStatus(OrderStatus.PROCESSING),
                    "totalCustomers", userService.countUsersByRole(UserRole.CUSTOMER),
                    "activeUsers", userService.countActiveUsers(),
                    "avgResponseTime", "2.5 hours", // Placeholder
                    "ticketsToday", 15 // Placeholder
            );

            return ResponseEntity.ok(new ApiResponse("Support dashboard retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get support dashboard: " + e.getMessage(), null));
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<User> users;

            if (search != null && !search.trim().isEmpty()) {
                users = userService.searchUsers(search, pageable);
            } else {
                users = userService.getUsersByRole(UserRole.CUSTOMER, pageable);
            }

            Page<UserResponse> response = users.map(UserResponse::new);
            return ResponseEntity.ok(new ApiResponse("Customers retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get customers: " + e.getMessage(), null));
        }
    }

    @GetMapping("/customers/{userId}")
    public ResponseEntity<?> getCustomerDetails(@PathVariable Long userId) {
        try {
            User customer = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            if(customer.getRole() != UserRole.CUSTOMER) return ResponseEntity
                    .badRequest().body(new ApiResponse("User with id " + userId + " is not a customer", null));

            // Get customer order statistics
            Long totalOrders = orderService.countOrdersByUserId(userId);
            java.math.BigDecimal totalSpent = orderService.getTotalSpentByUser(userId);

            Map<String, Object> customerDetails = Map.of(
                    "customer", new UserResponse(customer),
                    "totalOrders", totalOrders,
                    "totalSpent", totalSpent,
                    "joinDate", customer.getCreatedAt(),
                    "isActive", customer.getIsActive()
            );

            return ResponseEntity.ok(new ApiResponse("Customer details retrieved successfully", customerDetails));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get customer details: " + e.getMessage(), null));
        }
    }

    @GetMapping("/customers/{userId}/orders")
    public ResponseEntity<?> getCustomerOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User customer = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            if(customer.getRole() != UserRole.CUSTOMER) return ResponseEntity
                    .badRequest().body(new ApiResponse("User with id " + userId + " is not a customer", null));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Order> orders = orderService.getUserOrders(userId, pageable);
            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Customer orders retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get customer orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrdersForSupport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Order> orders;

            if (status != null) {
                orders = orderService.getOrdersByStatus(status, pageable);
            } else {
                orders = orderService.getAllOrders(pageable);
            }

            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);
            return ResponseEntity.ok(new ApiResponse("Orders retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<?> getPendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            Page<Order> orders = orderService.getOrdersByStatus(OrderStatus.PENDING, pageable);
            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Pending orders retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get pending orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/orders/urgent")
    public ResponseEntity<?> getUrgentOrders() {
        try {
            // Orders older than 24 hours that are still pending
            LocalDateTime urgentThreshold = LocalDateTime.now().minusDays(1);
            List<Order> orders = orderService.getOrdersBetweenDates(LocalDateTime.MIN, urgentThreshold)
                    .stream()
                    .filter(order -> order.getStatus() == OrderStatus.PENDING)
                    .toList();

            List<OrderSummaryResponse> response = orders.stream()
                    .map(OrderSummaryResponse::new)
                    .toList();

            return ResponseEntity.ok(new ApiResponse("Urgent orders retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get urgent orders: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @PutMapping("/orders/{orderId}/priority")
    public ResponseEntity<?> updateOrderPriority(
            @PathVariable Long orderId,
            @RequestParam String priority) {
        try {
            // In real implementation, add priority field to Order model
            // For now, just acknowledge the request
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Map<String, Object> result = Map.of(
                    "orderId", orderId,
                    "orderNumber", order.getOrderNumber(),
                    "priority", priority,
                    "updatedAt", LocalDateTime.now()
            );

            return ResponseEntity.ok(new ApiResponse("Order priority updated successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update order priority: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @PostMapping("/customers/{userId}/note")
    public ResponseEntity<?> addCustomerNote(
            @PathVariable Long userId,
            @RequestParam String note,
            @RequestParam(required = false) String category) {
        try {
            User customer = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // In real implementation, save note to customer_notes table
            Map<String, Object> noteData = Map.of(
                    "customerId", userId,
                    "customerName", customer.getName(),
                    "note", note,
                    "category", category != null ? category : "general",
                    "createdAt", LocalDateTime.now(),
                    "status", "saved"
            );

            return ResponseEntity.ok(new ApiResponse("Customer note added successfully", noteData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to add customer note: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @GetMapping("/customers/{userId}/notes")
    public ResponseEntity<?> getCustomerNotes(@PathVariable Long userId) {
        try {
            User customer = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // In real implementation, retrieve notes from customer_notes table
            List<Map<String, Object>> notes = List.of(
                    Map.of(
                            "id", 1,
                            "note", "Customer called regarding delivery delay",
                            "category", "delivery",
                            "createdAt", LocalDateTime.now().minusDays(2),
                            "createdBy", "support@ecommerce.com"
                    ),
                    Map.of(
                            "id", 2,
                            "note", "Resolved payment issue",
                            "category", "payment",
                            "createdAt", LocalDateTime.now().minusDays(5),
                            "createdBy", "support@ecommerce.com"
                    )
            );

            return ResponseEntity.ok(new ApiResponse("Customer notes retrieved successfully", notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get customer notes: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @GetMapping("/statistics")
    public ResponseEntity<?> getSupportStatistics() {
        try {
            Map<String, Object> stats = Map.of(
                    "totalTicketsToday", 25,
                    "resolvedTicketsToday", 18,
                    "averageResponseTime", "2.3 hours",
                    "customerSatisfactionRate", "94%",
                    "pendingTickets", 7,
                    "escalatedTickets", 2
            );

            return ResponseEntity.ok(new ApiResponse("Support statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get support statistics: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @PostMapping("/orders/{orderId}/refund")
    public ResponseEntity<?> initiateRefund(
            @PathVariable Long orderId,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String reason) {
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // In real implementation, process refund through payment gateway
            Map<String, Object> refundData = Map.of(
                    "orderId", orderId,
                    "orderNumber", order.getOrderNumber(),
                    "refundAmount", amount,
                    "reason", reason,
                    "status", "initiated",
                    "processedAt", LocalDateTime.now()
            );

            return ResponseEntity.ok(new ApiResponse("Refund initiated successfully", refundData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to initiate refund: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Implement Before Deployment
    @GetMapping("/reports/daily")
    public ResponseEntity<?> getDailySupportReport(@RequestParam(required = false) String date) {
        try {
            LocalDateTime reportDate = date != null ? LocalDateTime.parse(date) : LocalDateTime.now();

            Map<String, Object> report = Map.of(
                    "date", reportDate.toLocalDate(),
                    "totalTickets", 25,
                    "resolvedTickets", 18,
                    "pendingTickets", 7,
                    "avgResolutionTime", "3.2 hours",
                    "customerCalls", 15,
                    "emailSupport", 10
            );

            return ResponseEntity.ok(new ApiResponse("Daily support report generated", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to generate report: " + e.getMessage(), null));
        }
    }
}