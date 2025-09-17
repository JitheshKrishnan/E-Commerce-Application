package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.CreateOrderRequest;
import com.example.ecommerce.backend.dto.OrderResponse;
import com.example.ecommerce.backend.dto.OrderSummaryResponse;
import com.example.ecommerce.backend.model.Order;
import com.example.ecommerce.backend.model.OrderStatus;
import com.example.ecommerce.backend.model.PaymentStatus;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.service.OrderService;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // Customer endpoints
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = orderService.createOrderFromCart(
                    user.getId(),
                    request.getShippingAddress(),
                    request.getPaymentMethod()
            );

            OrderResponse response = new OrderResponse(order);
            return ResponseEntity.ok(new ApiResponse("Order created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create order: " + e.getMessage(), null));
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Sort sort = Sort.by("createdAt").descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Order> orders = orderService.getUserOrders(user.getId(), pageable);

            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);
            return ResponseEntity.ok(new ApiResponse("Orders retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{orderNumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrder(@PathVariable String orderNumber, Authentication auth) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Check if user owns this order (unless admin/support)
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRole().name().equals("ADMIN") &&
                    !user.getRole().name().equals("SUPPORT") &&
                    !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse("Access denied", null));
            }

            OrderResponse response = new OrderResponse(order);
            return ResponseEntity.ok(new ApiResponse("Order retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get order: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Authentication auth) {
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Check if user owns this order (unless admin)
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRole().name().equals("ADMIN") &&
                    !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse("Access denied", null));
            }

            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to cancel order: " + e.getMessage(), null));
        }
    }

    // Support/Admin endpoints
    @GetMapping("/manage")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus) {
        try {
            Sort sort = Sort.by("createdAt").descending();
            Pageable pageable = PageRequest.of(page, size, sort);

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

    @GetMapping("/search")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> searchOrders(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.searchOrders(searchTerm, pageable);
            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Search completed", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Search failed: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            OrderResponse response = new OrderResponse(order);

            return ResponseEntity.ok(new ApiResponse("Order status updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update order status: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{orderId}/payment-status")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long orderId, @RequestParam PaymentStatus paymentStatus) {
        try {
            Order order = orderService.updatePaymentStatus(orderId, paymentStatus);
            OrderResponse response = new OrderResponse(order);

            return ResponseEntity.ok(new ApiResponse("Payment status updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update payment status: " + e.getMessage(), null));
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> filterOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Order> orders = orderService.getOrdersWithFilters(userId, status, paymentStatus, start, end, pageable);
            Page<OrderSummaryResponse> response = orders.map(OrderSummaryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Orders filtered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to filter orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderStats() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("pendingOrders", orderService.countOrdersByStatus(OrderStatus.PENDING));
            stats.put("confirmedOrders", orderService.countOrdersByStatus(OrderStatus.CONFIRMED));
            stats.put("shippedOrders", orderService.countOrdersByStatus(OrderStatus.SHIPPED));
            stats.put("deliveredOrders", orderService.countOrdersByStatus(OrderStatus.DELIVERED));
            stats.put("cancelledOrders", orderService.countOrdersByStatus(OrderStatus.CANCELLED));
            stats.put("averageOrderValue", orderService.getAverageOrderValue());

            return ResponseEntity.ok(new ApiResponse("Order stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get order stats: " + e.getMessage(), null));
        }
    }
}