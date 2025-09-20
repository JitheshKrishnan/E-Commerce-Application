package com.example.ecommerce.backend.model;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("Customer", "Regular customer with shopping privileges"),
    ADMIN("Administrator", "System administrator with full access"),
    SELLER("Seller", "Vendor who can manage products and inventory"),
    SUPPORT("Support Staff", "Customer support staff with order management access");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}