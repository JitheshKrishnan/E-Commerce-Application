package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private UserResponse user;
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private String status;
    private String paymentStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.user = new UserResponse(order.getUser());
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
        this.totalPrice = order.getTotalPrice();
        this.taxAmount = order.getTaxAmount();
        this.shippingCost = order.getShippingCost();
        this.discountAmount = order.getDiscountAmount();
        this.status = order.getStatus().name();
        this.paymentStatus = order.getPaymentStatus().name();
        this.shippingAddress = order.getShippingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.notes = order.getNotes();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
