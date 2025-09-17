package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.CartItem;
import com.example.ecommerce.backend.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    // Basic cart operations
    CartItem addToCart(Long userId, Long productId, Integer quantity);
    CartItem updateCartItem(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);

    // Cart retrieval
    List<CartItem> getCartItems(Long userId);
    CartItem getCartItem(Long userId, Long productId);

    // Cart calculations
    BigDecimal getCartTotal(Long userId);
    Integer getCartItemCount(Long userId);
    Integer getTotalQuantity(Long userId);

    // Cart validation
    boolean isProductInCart(Long userId, Long productId);
    boolean validateCartForCheckout(Long userId);

    // Cleanup operations
    void removeExpiredCartItems();
    void syncCartWithInventory(Long userId);
}