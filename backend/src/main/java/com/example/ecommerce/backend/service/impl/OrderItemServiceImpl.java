package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.OrderItem;
import com.example.ecommerce.backend.repository.OrderItemRepository;
import com.example.ecommerce.backend.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getBestSellingProducts(Pageable pageable) {
        return orderItemRepository.findBestSellingProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getBestSellingProductsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderItemRepository.findBestSellingProductsBetweenDates(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalQuantitySoldByProduct(Long productId) {
        Long quantity = orderItemRepository.getTotalQuantitySoldByProductId(productId);
        return quantity != null ? quantity : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByProduct(Long productId) {
        BigDecimal revenue = orderItemRepository.getTotalRevenueByProductId(productId);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryAnalytics() {
        return orderItemRepository.getCategoryAnalytics();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = orderItemRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItem> getUserPurchaseHistory(Long userId, Pageable pageable) {
        return orderItemRepository.findUserPurchaseHistory(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPendingQuantityByProduct() {
        return orderItemRepository.findPendingQuantityByProduct();
    }
}