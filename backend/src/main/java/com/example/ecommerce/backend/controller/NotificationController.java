//package com.example.ecommerce.backend.controller;
//
//import com.example.ecommerce.backend.dto.ApiResponse;
//import com.example.ecommerce.backend.dto.BulkEmailRequest;
//import com.example.ecommerce.backend.dto.EmailRequest;
//import com.example.ecommerce.backend.dto.SmsRequest;
//import com.example.ecommerce.backend.model.Order;
//import com.example.ecommerce.backend.model.User;
//import com.example.ecommerce.backend.service.NotificationService;
//import com.example.ecommerce.backend.service.OrderService;
//import com.example.ecommerce.backend.service.UserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/notifications")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class NotificationController {
//
//    private NotificationService notificationService;
//    private UserService userService;
//    private OrderService orderService;
//
//    @PostMapping("/send-email")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> sendCustomEmail(@Valid @RequestBody EmailRequest request) {
//        try {
//            // Validate recipient user exists
//            User recipient = userService.getUserByEmail(request.getRecipientEmail())
//                    .orElseThrow(() -> new RuntimeException("Recipient not found"));
//
//            // In real implementation, use actual email service
//            // notificationService.sendCustomEmail(request.getRecipientEmail(), request.getSubject(), request.getContent());
//
//            Map<String, Object> result = Map.of(
//                    "recipient", request.getRecipientEmail(),
//                    "subject", request.getSubject(),
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now(),
//                    "messageId", "msg_" + System.currentTimeMillis()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Email sent successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send email: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/send-sms")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> sendSms(@Valid @RequestBody SmsRequest request) {
//        try {
//            // Validate phone number format and user exists
//            User recipient = userService.getUserById(request.getUserId())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            if (recipient.getPhoneNumber() == null || recipient.getPhoneNumber().trim().isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse("User does not have a phone number", null));
//            }
//
//            // In real implementation, use SMS service like Twilio
//            // notificationService.sendSMS(recipient.getPhoneNumber(), request.getMessage());
//
//            Map<String, Object> result = Map.of(
//                    "userId", request.getUserId(),
//                    "phoneNumber", recipient.getPhoneNumber(),
//                    "message", request.getMessage(),
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now(),
//                    "smsId", "sms_" + System.currentTimeMillis()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("SMS sent successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send SMS: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/email/welcome/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> sendWelcomeEmail(@PathVariable Long userId) {
//        try {
//            User user = userService.getUserById(userId)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            notificationService.sendWelcomeEmail(user);
//
//            Map<String, Object> result = Map.of(
//                    "userId", userId,
//                    "email", user.getEmail(),
//                    "type", "welcome",
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Welcome email sent successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send welcome email: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/email/order-confirmation/{orderId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> sendOrderConfirmationEmail(@PathVariable Long orderId) {
//        try {
//            Order order = orderService.getOrderById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            notificationService.sendOrderConfirmationEmail(order);
//
//            Map<String, Object> result = Map.of(
//                    "orderId", orderId,
//                    "orderNumber", order.getOrderNumber(),
//                    "customerEmail", order.getUser().getEmail(),
//                    "type", "order_confirmation",
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Order confirmation email sent", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send order confirmation: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/email/order-update/{orderId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> sendOrderStatusUpdateEmail(@PathVariable Long orderId) {
//        try {
//            Order order = orderService.getOrderById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            notificationService.sendOrderStatusUpdateEmail(order);
//
//            Map<String, Object> result = Map.of(
//                    "orderId", orderId,
//                    "orderNumber", order.getOrderNumber(),
//                    "status", order.getStatus().name(),
//                    "customerEmail", order.getUser().getEmail(),
//                    "type", "order_update",
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Order status update email sent", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send order update: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/low-stock-alert")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> sendLowStockAlert() {
//        try {
//            notificationService.notifyLowStock();
//
//            Map<String, Object> result = Map.of(
//                    "type", "low_stock_alert",
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Low stock alert sent successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send low stock alert: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/bulk-email")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> sendBulkEmail(@Valid @RequestBody BulkEmailRequest request) {
//        try {
//            List<User> recipients;
//
//            if (request.getUserRole() != null) {
//                recipients = userService.getUsersByRole(request.getUserRole());
//            } else if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
//                recipients = request.getUserIds().stream()
//                        .map(id -> userService.getUserById(id).orElse(null))
//                        .filter(user -> user != null)
//                        .toList();
//            } else {
//                recipients = userService.getActiveUsers();
//            }
//
//            int successCount = 0;
//            int failureCount = 0;
//
//            for (User recipient : recipients) {
//                try {
//                    // In real implementation, send actual email
//                    // notificationService.sendEmail(recipient.getEmail(), request.getSubject(), request.getContent());
//                    successCount++;
//                } catch (Exception e) {
//                    failureCount++;
//                }
//            }
//
//            Map<String, Object> result = Map.of(
//                    "totalRecipients", recipients.size(),
//                    "successful", successCount,
//                    "failed", failureCount,
//                    "subject", request.getSubject(),
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Bulk email sent", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send bulk email: " + e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/templates")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> getEmailTemplates() {
//        try {
//            List<Map<String, Object>> templates = List.of(
//                    Map.of(
//                            "id", "welcome",
//                            "name", "Welcome Email",
//                            "subject", "Welcome to Our Store!",
//                            "category", "user_management"
//                    ),
//                    Map.of(
//                            "id", "order_confirmation",
//                            "name", "Order Confirmation",
//                            "subject", "Order Confirmation - {{orderNumber}}",
//                            "category", "orders"
//                    ),
//                    Map.of(
//                            "id", "order_shipped",
//                            "name", "Order Shipped",
//                            "subject", "Your Order Has Been Shipped",
//                            "category", "orders"
//                    ),
//                    Map.of(
//                            "id", "password_reset",
//                            "name", "Password Reset",
//                            "subject", "Reset Your Password",
//                            "category", "security"
//                    ),
//                    Map.of(
//                            "id", "promotional",
//                            "name", "Promotional Email",
//                            "subject", "Special Offer Just For You!",
//                            "category", "marketing"
//                    )
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Email templates retrieved", templates));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to get templates: " + e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/history")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
//    public ResponseEntity<?> getNotificationHistory(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(required = false) String type) {
//        try {
//            // In real implementation, get from notification_history table
//            List<Map<String, Object>> history = List.of(
//                    Map.of(
//                            "id", 1,
//                            "type", "email",
//                            "recipient", "customer@example.com",
//                            "subject", "Order Confirmation",
//                            "status", "delivered",
//                            "sentAt", LocalDateTime.now().minusDays(1)
//                    ),
//                    Map.of(
//                            "id", 2,
//                            "type", "sms",
//                            "recipient", "+1234567890",
//                            "message", "Your order has been shipped",
//                            "status", "delivered",
//                            "sentAt", LocalDateTime.now().minusDays(2)
//                    )
//            );
//
//            Map<String, Object> result = Map.of(
//                    "notifications", history,
//                    "totalElements", history.size(),
//                    "currentPage", page,
//                    "pageSize", size
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Notification history retrieved", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to get notification history: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/test-email")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> sendTestEmail(@RequestParam String email) {
//        try {
//            Map<String, Object> result = Map.of(
//                    "testEmail", email,
//                    "subject", "Test Email from E-commerce System",
//                    "content", "This is a test email to verify email delivery.",
//                    "status", "sent",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Test email sent successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to send test email: " + e.getMessage(), null));
//        }
//    }
//}