package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.OrderStatus;
import com.example.ecommerce.backend.model.UserRole;
import com.example.ecommerce.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderService.getTotalRevenueBetweenDates(startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> getRevenueByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> categoryData = orderItemService.getCategoryAnalytics();
        Map<String, BigDecimal> revenueByCategory = new HashMap<>();

        for (Object[] data : categoryData) {
            String category = (String) data[0];
            BigDecimal revenue = (BigDecimal) data[2];
            revenueByCategory.put(category, revenue);
        }

        return revenueByCategory;
    }

    @Override
    public Map<String, Long> getOrderCountByStatus() {
        Map<String, Long> orderCounts = new HashMap<>();

        for (OrderStatus status : OrderStatus.values()) {
            Long count = orderService.countOrdersByStatus(status);
            orderCounts.put(status.name(), count);
        }

        return orderCounts;
    }

    @Override
    public List<Object[]> getTopSellingProducts(int limit) {
        // Implementation would use PageRequest to limit results
        return orderItemService.getBestSellingProducts(
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }

    @Override
    public List<Object[]> getTopCustomers(int limit) {
        return orderService.getTopCustomersByRevenue(
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }

    @Override
    public Map<String, Long> getInventoryStatus() {
        Map<String, Long> inventoryStatus = new HashMap<>();

        inventoryStatus.put("totalItems", inventoryService.getTotalInventoryQuantity());
        inventoryStatus.put("lowStockItems", inventoryService.countLowStockItems());
        inventoryStatus.put("outOfStockItems", inventoryService.countOutOfStockItems());
        inventoryStatus.put("reservedItems", inventoryService.getTotalReservedQuantity());

        return inventoryStatus;
    }

    @Override
    public List<Object[]> getCriticalStockItems() {
        return inventoryService.getLowStockItems().stream()
                .map(inventory -> new Object[]{
                        inventory.getProduct().getId(),
                        inventory.getProduct().getTitle(),
                        inventory.getQtyAvailable(),
                        inventory.getReorderLevel()
                })
                .toList();
    }

    @Override
    public Long getTotalActiveUsers() {
        return userService.countActiveUsers();
    }

    @Override
    public Map<String, Long> getUsersByRole() {
        Map<String, Long> usersByRole = new HashMap<>();

        for (UserRole role : UserRole.values()) {
            Long count = userService.countUsersByRole(role);
            usersByRole.put(role.name(), count);
        }

        return usersByRole;
    }

    @Override
    public List<Object[]> getNewUsersOverTime(LocalDateTime startDate, LocalDateTime endDate) {
        // This would typically group users by date - simplified implementation
        List<com.example.ecommerce.backend.model.User> users = userService.getUsersCreatedBetween(startDate, endDate);
        return users.stream()
                .map(user -> new Object[]{
                        user.getCreatedAt().toLocalDate(),
                        user.getName(),
                        user.getEmail()
                })
                .toList();
    }

    @Override
    public BigDecimal getAverageOrderValue() {
        return orderService.getAverageOrderValue();
    }

    //TODO: Check The Necessity For This Method
    @Override
    public Double getCartConversionRate() {
        // This is a simplified calculation - in reality, you'd track cart creation vs orders
        // For now, returning a placeholder calculation
        Long totalOrders = orderService.countOrdersByStatus(OrderStatus.DELIVERED);
        Long totalUsers = userService.countActiveUsers();

        if (totalUsers == 0) return 0.0;
        return (totalOrders.doubleValue() / totalUsers.doubleValue()) * 100;
    }

    @Override
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Time range for recent metrics (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();

        metrics.put("totalRevenue", getTotalRevenue(thirtyDaysAgo, now));
        metrics.put("totalOrders", orderService.countOrdersByStatus(OrderStatus.DELIVERED));
        metrics.put("averageOrderValue", getAverageOrderValue());
        metrics.put("totalActiveUsers", getTotalActiveUsers());
        metrics.put("totalProducts", productService.countActiveProducts());
        metrics.put("lowStockItems", inventoryService.countLowStockItems());
        metrics.put("outOfStockItems", inventoryService.countOutOfStockItems());
        metrics.put("pendingOrders", orderService.countOrdersByStatus(OrderStatus.PENDING));
        metrics.put("cartConversionRate", getCartConversionRate());

        return metrics;
    }
}