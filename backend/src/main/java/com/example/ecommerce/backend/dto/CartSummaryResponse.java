package com.example.ecommerce.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class CartSummaryResponse {
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private Integer totalQuantity;

    public CartSummaryResponse(List<CartItemResponse> items, BigDecimal totalAmount, Integer totalItems) {
        this.items = items;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.totalQuantity = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
    }
}