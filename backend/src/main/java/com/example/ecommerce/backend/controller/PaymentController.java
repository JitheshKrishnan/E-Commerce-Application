//package com.example.ecommerce.backend.controller;
//
//import com.example.ecommerce.backend.dto.ApiResponse;
//import com.example.ecommerce.backend.dto.ProcessPaymentRequest;
//import com.example.ecommerce.backend.model.Order;
//import com.example.ecommerce.backend.model.PaymentStatus;
//import com.example.ecommerce.backend.model.User;
//import com.example.ecommerce.backend.service.NotificationService;
//import com.example.ecommerce.backend.service.OrderService;
//import com.example.ecommerce.backend.service.UserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/payments")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class PaymentController {
//
//    private OrderService orderService;
//    private NotificationService notificationService;
//    private UserService userService;
//
//    @PostMapping("/process")
//    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
//    public ResponseEntity<?> processPayment(@Valid @RequestBody ProcessPaymentRequest request, Authentication auth) {
//        try {
//            // Validate order exists and belongs to user
//            Order order = orderService.getOrderById(request.getOrderId())
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            User user = userService.getUserByEmail(auth.getName())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Check if user owns this order (unless admin)
//            if (!user.getRole().name().equals("ADMIN") &&
//                    !order.getUser().getId().equals(user.getId())) {
//                return ResponseEntity.status(403)
//                        .body(new ApiResponse("Access denied", null));
//            }
//
//            if (!orderService.isValidOrderForPayment(request.getOrderId())) {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse("Order is not valid for payment", null));
//            }
//
//            // Process payment with gateway
//            PaymentResult paymentResult = processPaymentWithGateway(request, order);
//
//            if (paymentResult.isSuccess()) {
//                // Update order payment status
//                order = orderService.updatePaymentStatus(request.getOrderId(), PaymentStatus.PAID);
//
//                // Send confirmation email
//                notificationService.sendPaymentConfirmationEmail(order);
//
//                Map<String, Object> response = Map.of(
//                        "orderNumber", order.getOrderNumber(),
//                        "paymentId", paymentResult.getPaymentId(),
//                        "amount", order.getTotalPrice(),
//                        "status", "success",
//                        "transactionDate", LocalDateTime.now()
//                );
//
//                return ResponseEntity.ok(new ApiResponse("Payment processed successfully", response));
//            } else {
//                // Update payment status to failed
//                orderService.updatePaymentStatus(request.getOrderId(), PaymentStatus.FAILED);
//
//                Map<String, Object> errorResponse = Map.of(
//                        "orderNumber", order.getOrderNumber(),
//                        "error", paymentResult.getErrorMessage(),
//                        "status", "failed",
//                        "transactionDate", LocalDateTime.now()
//                );
//
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse("Payment processing failed", errorResponse));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Payment processing error: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/refund")
//    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
//    public ResponseEntity<?> processRefund(
//            @RequestParam Long orderId,
//            @RequestParam BigDecimal amount,
//            @RequestParam String reason) {
//        try {
//            Order order = orderService.getOrderById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            if (order.getPaymentStatus() != PaymentStatus.PAID) {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse("Order payment is not eligible for refund", null));
//            }
//
//            // Process refund with payment gateway
//            RefundResult refundResult = processRefundWithGateway(order, amount, reason);
//
//            if (refundResult.isSuccess()) {
//                // Update order payment status
//                if (amount.compareTo(order.getTotalPrice()) == 0) {
//                    orderService.updatePaymentStatus(orderId, PaymentStatus.REFUNDED);
//                } else {
//                    // Partial refund - would need a more sophisticated status system
//                    orderService.updatePaymentStatus(orderId, PaymentStatus.PARTIALLY_REFUNDED);
//                }
//
//                Map<String, Object> response = Map.of(
//                        "orderNumber", order.getOrderNumber(),
//                        "refundId", refundResult.getRefundId(),
//                        "refundAmount", amount,
//                        "reason", reason,
//                        "status", "success",
//                        "processedAt", LocalDateTime.now()
//                );
//
//                return ResponseEntity.ok(new ApiResponse("Refund processed successfully", response));
//            } else {
//                return ResponseEntity.badRequest()
//                        .body(new ApiResponse("Refund processing failed: " + refundResult.getErrorMessage(), null));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Refund processing error: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/webhook/stripe")
//    public ResponseEntity<?> handleStripeWebhook(
//            @RequestBody String payload,
//            @RequestHeader("Stripe-Signature") String signature) {
//        try {
//            // In real implementation:
//            // 1. Verify webhook signature
//            // 2. Parse webhook payload
//            // 3. Handle different event types
//            // 4. Update order status accordingly
//
//            Map<String, Object> result = Map.of(
//                    "status", "received",
//                    "timestamp", LocalDateTime.now(),
//                    "processed", true
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Webhook processed successfully", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Webhook processing failed: " + e.getMessage(), null));
//        }
//    }
//
//    @PostMapping("/webhook/paypal")
//    public ResponseEntity<?> handlePayPalWebhook(@RequestBody String payload) {
//        try {
//            // Handle PayPal webhook events
//            Map<String, Object> result = Map.of(
//                    "status", "received",
//                    "provider", "paypal",
//                    "timestamp", LocalDateTime.now()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("PayPal webhook processed", result));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("PayPal webhook processing failed: " + e.getMessage(), null));
//        }
//    }
//
//    //TODO: Check With SecurityConfig
//    @GetMapping("/methods")
//    public ResponseEntity<?> getPaymentMethods() {
//        try {
//            List<Map<String, Object>> methods = List.of(
//                    Map.of(
//                            "id", "credit_card",
//                            "name", "Credit Card",
//                            "description", "Visa, MasterCard, American Express",
//                            "enabled", true,
//                            "processingFee", "2.9%"
//                    ),
//                    Map.of(
//                            "id", "debit_card",
//                            "name", "Debit Card",
//                            "description", "Direct bank account debit",
//                            "enabled", true,
//                            "processingFee", "1.5%"
//                    ),
//                    Map.of(
//                            "id", "paypal",
//                            "name", "PayPal",
//                            "description", "Pay with your PayPal account",
//                            "enabled", true,
//                            "processingFee", "3.49%"
//                    ),
//                    Map.of(
//                            "id", "bank_transfer",
//                            "name", "Bank Transfer",
//                            "description", "Direct bank to bank transfer",
//                            "enabled", true,
//                            "processingFee", "0%"
//                    ),
//                    Map.of(
//                            "id", "cash_on_delivery",
//                            "name", "Cash on Delivery",
//                            "description", "Pay when you receive your order",
//                            "enabled", true,
//                            "processingFee", "0%"
//                    )
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Payment methods retrieved successfully", methods));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to get payment methods: " + e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/transactions/{orderId}")
//    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SUPPORT') or hasRole('ADMIN')")
//    public ResponseEntity<?> getPaymentTransactions(@PathVariable Long orderId, Authentication auth) {
//        try {
//            Order order = orderService.getOrderById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            User user = userService.getUserByEmail(auth.getName())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Check access permissions
//            if (!user.getRole().name().equals("ADMIN") &&
//                    !user.getRole().name().equals("SUPPORT") &&
//                    !order.getUser().getId().equals(user.getId())) {
//                return ResponseEntity.status(403)
//                        .body(new ApiResponse("Access denied", null));
//            }
//
//            // In real implementation, get actual transaction history from payment provider
//            List<Map<String, Object>> transactions = List.of(
//                    Map.of(
//                            "id", "txn_001",
//                            "type", "payment",
//                            "amount", order.getTotalPrice(),
//                            "status", order.getPaymentStatus().name(),
//                            "method", order.getPaymentMethod(),
//                            "timestamp", order.getCreatedAt(),
//                            "gateway", "stripe"
//                    )
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Payment transactions retrieved", transactions));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to get transactions: " + e.getMessage(), null));
//        }
//    }
//
//    @GetMapping("/status/{orderId}")
//    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SUPPORT') or hasRole('ADMIN')")
//    public ResponseEntity<?> getPaymentStatus(@PathVariable Long orderId, Authentication auth) {
//        try {
//            Order order = orderService.getOrderById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            User user = userService.getUserByEmail(auth.getName())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // Check access permissions
//            if (!user.getRole().name().equals("ADMIN") &&
//                    !user.getRole().name().equals("SUPPORT") &&
//                    !order.getUser().getId().equals(user.getId())) {
//                return ResponseEntity.status(403)
//                        .body(new ApiResponse("Access denied", null));
//            }
//
//            Map<String, Object> paymentInfo = Map.of(
//                    "orderNumber", order.getOrderNumber(),
//                    "totalAmount", order.getTotalPrice(),
//                    "paymentStatus", order.getPaymentStatus().name(),
//                    "paymentMethod", order.getPaymentMethod(),
//                    "lastUpdated", order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt()
//            );
//
//            return ResponseEntity.ok(new ApiResponse("Payment status retrieved", paymentInfo));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse("Failed to get payment status: " + e.getMessage(), null));
//        }
//    }
//
//    // Helper methods for payment processing
//    private PaymentResult processPaymentWithGateway(ProcessPaymentRequest request, Order order) {
//        try {
//            // Mock payment processing - In real implementation, integrate with:
//            // - Stripe API
//            // - PayPal API
//            // - Square API
//            // - Other payment gateways
//
//            Thread.sleep(2000); // Simulate processing time
//
//            // Mock failure for cards ending in 0000
//            boolean success = !request.getCardNumber().endsWith("0000");
//
//            if (success) {
//                return new PaymentResult(true, "pay_" + System.currentTimeMillis(), null);
//            } else {
//                return new PaymentResult(false, null, "Card declined");
//            }
//        } catch (InterruptedException e) {
//            return new PaymentResult(false, null, "Processing timeout");
//        }
//    }
//
//    private RefundResult processRefundWithGateway(Order order, BigDecimal amount, String reason) {
//        try {
//            // Mock refund processing
//            Thread.sleep(1000);
//
//            return new RefundResult(true, "ref_" + System.currentTimeMillis(), null);
//        } catch (InterruptedException e) {
//            return new RefundResult(false, null, "Refund timeout");
//        }
//    }
//
//    // Helper classes for payment results
//    private static class PaymentResult {
//        private final boolean success;
//        private final String paymentId;
//        private final String errorMessage;
//
//        public PaymentResult(boolean success, String paymentId, String errorMessage) {
//            this.success = success;
//            this.paymentId = paymentId;
//            this.errorMessage = errorMessage;
//        }
//
//        public boolean isSuccess() { return success; }
//        public String getPaymentId() { return paymentId; }
//        public String getErrorMessage() { return errorMessage; }
//    }
//
//    private static class RefundResult {
//        private final boolean success;
//        private final String refundId;
//        private final String errorMessage;
//
//        public RefundResult(boolean success, String refundId, String errorMessage) {
//            this.success = success;
//            this.refundId = refundId;
//            this.errorMessage = errorMessage;
//        }
//
//        public boolean isSuccess() { return success; }
//        public String getRefundId() { return refundId; }
//        public String getErrorMessage() { return errorMessage; }
//    }
//}