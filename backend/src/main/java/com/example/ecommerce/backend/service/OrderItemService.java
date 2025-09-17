package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    // Basic CRUD operations
    OrderItem createOrderItem(OrderItem orderItem);
    Optional<OrderItem> getOrderItemById(Long id);
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    List<OrderItem> getOrderItemsByProductId(Long productId);

    // Analytics
    Page<Object[]> getBestSellingProducts(Pageable pageable);
    Page<Object[]> getBestSellingProductsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Long getTotalQuantitySoldByProduct(Long productId);
    BigDecimal getTotalRevenueByProduct(Long productId);
    List<Object[]> getCategoryAnalytics();
    BigDecimal getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    // User purchase history
    Page<OrderItem> getUserPurchaseHistory(Long userId, Pageable pageable);
    List<Object[]> getPendingQuantityByProduct();
}