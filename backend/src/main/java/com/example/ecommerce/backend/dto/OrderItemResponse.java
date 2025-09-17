package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Order Response DTOs
@Data
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String productTitle;
    private String productSku;

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.product = new ProductResponse(orderItem.getProduct());
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.totalPrice = orderItem.getTotalPrice();
        this.productTitle = orderItem.getProductTitle();
        this.productSku = orderItem.getProductSku();
    }
}