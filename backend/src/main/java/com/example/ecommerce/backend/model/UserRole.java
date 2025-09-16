package com.example.ecommerce.backend.model;

import lombok.Getter;

@Getter
public enum UserRole {
    CUSTOMER("Customer"),
    ADMIN("Administrator"),
    SELLER("Seller"),
    SUPPORT("Support Staff");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}