package com.example.ecommerce.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    // Sales Analytics
    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, BigDecimal> getRevenueByCategory(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Long> getOrderCountByStatus();
    List<Object[]> getTopSellingProducts(int limit);
    List<Object[]> getTopCustomers(int limit);

    // Inventory Analytics
    Map<String, Long> getInventoryStatus();
    List<Object[]> getCriticalStockItems();

    // User Analytics
    Long getTotalActiveUsers();
    Map<String, Long> getUsersByRole();
    List<Object[]> getNewUsersOverTime(LocalDateTime startDate, LocalDateTime endDate);

    // Performance Metrics
    BigDecimal getAverageOrderValue();
    Double getCartConversionRate();
    Map<String, Object> getDashboardMetrics();
}