package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.CartItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Cart Response DTOs
@Data
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.product = new ProductResponse(cartItem.getProduct());
        this.quantity = cartItem.getQuantity();
        this.unitPrice = cartItem.getProduct().getPrice();
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.createdAt = cartItem.getCreatedAt();
    }
}
