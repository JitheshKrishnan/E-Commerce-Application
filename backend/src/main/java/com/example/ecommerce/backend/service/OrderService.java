package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.Order;
import com.example.ecommerce.backend.model.OrderStatus;
import com.example.ecommerce.backend.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Order creation and management
    Order createOrderFromCart(Long userId, String shippingAddress, String paymentMethod);
    Order createOrder(Order order);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNumber(String orderNumber);
    Order updateOrder(Order order);
    void cancelOrder(Long orderId);

    // Order status management
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
    Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    // Order retrieval
    List<Order> getAllOrders();
    Page<Order> getAllOrders(Pageable pageable);
    List<Order> getUserOrders(Long userId);
    Page<Order> getUserOrders(Long userId, Pageable pageable);
    List<Order> getOrdersByStatus(OrderStatus status);
    Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable);

    // Search and filter
    Page<Order> searchOrders(String searchTerm, Pageable pageable);
    Page<Order> getOrdersWithFilters(Long userId, OrderStatus status, PaymentStatus paymentStatus,
                                     LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<Order> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    // Analytics and reporting
    BigDecimal getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    Long countOrdersByStatus(OrderStatus status);
    BigDecimal getAverageOrderValue();
    BigDecimal getTotalSpentByUser(Long userId);
    Page<Object[]> getTopCustomersByRevenue(Pageable pageable);

    // Order calculations
    BigDecimal calculateOrderTotal(Long orderId);
    BigDecimal calculateTax(BigDecimal subtotal);
    BigDecimal calculateShipping(BigDecimal weight);

    // Validation
    boolean canCancelOrder(Long orderId);
    boolean isValidOrderForPayment(Long orderId);
    String generateOrderNumber();
}