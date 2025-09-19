package com.example.ecommerce.backend.model;

import java.util.List;
import java.util.Map;

public enum OrderStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    private static final Map<OrderStatus, List<OrderStatus>> VALID_TRANSITIONS = Map.of(
            PENDING, List.of(CONFIRMED, CANCELLED),
            CONFIRMED, List.of(PROCESSING, CANCELLED),
            PROCESSING, List.of(SHIPPED, CANCELLED),
            SHIPPED, List.of(DELIVERED, CANCELLED),
            DELIVERED, List.of(REFUNDED)
    );

    public boolean canTransitionTo(OrderStatus newStatus) {
        return VALID_TRANSITIONS.getOrDefault(this, List.of()).contains(newStatus);
    }
}