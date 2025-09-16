package com.example.ecommerce.backend.repository;

import com.example.ecommerce.backend.model.Order;
import com.example.ecommerce.backend.model.OrderItem;
import com.example.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Basic queries
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProduct(Product product);

    List<OrderItem> findByProductId(Long productId);

    // Order-specific queries
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Long countItemsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal getTotalAmountByOrderId(@Param("orderId") Long orderId);

    // Product analytics
    @Query("SELECT oi.product.id, oi.product.title, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product.id, oi.product.title " +
            "ORDER BY totalSold DESC")
    Page<Object[]> findBestSellingProducts(Pageable pageable);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.status = 'DELIVERED'")
    Long getTotalQuantitySoldByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId AND oi.order.status = 'DELIVERED'")
    BigDecimal getTotalRevenueByProductId(@Param("productId") Long productId);

    // Time-based analytics
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItem> findOrderItemsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT oi.product.id, oi.product.title, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = 'DELIVERED' AND oi.order.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.id, oi.product.title " +
            "ORDER BY totalSold DESC")
    Page<Object[]> findBestSellingProductsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Revenue analytics
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.status = 'DELIVERED' AND oi.order.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Category analytics (assuming category is accessible through product)
    @Query("SELECT oi.product.category, SUM(oi.quantity) as totalSold, SUM(oi.totalPrice) as totalRevenue " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = 'DELIVERED' " +
            "GROUP BY oi.product.category " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getCategoryAnalytics();

    // User purchase history
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId ORDER BY oi.order.createdAt DESC")
    Page<OrderItem> findUserPurchaseHistory(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT oi.product FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.order.status = 'DELIVERED'")
    List<Product> findProductsPurchasedByUser(@Param("userId") Long userId);

    // Inventory impact
    @Query("SELECT oi.product.id, SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED') GROUP BY oi.product.id")
    List<Object[]> findPendingQuantityByProduct();
}