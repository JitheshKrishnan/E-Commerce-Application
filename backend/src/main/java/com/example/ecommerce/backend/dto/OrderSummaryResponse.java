package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderSummaryResponse {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private Integer itemCount;
    private LocalDateTime createdAt;

    public OrderSummaryResponse(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.customerName = order.getUser().getName();
        this.customerEmail = order.getUser().getEmail();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().name();
        this.paymentStatus = order.getPaymentStatus().name();
        this.itemCount = order.getOrderItems().size();
        this.createdAt = order.getCreatedAt();
    }
}
