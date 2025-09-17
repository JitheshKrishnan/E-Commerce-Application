package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.CartItem;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.repository.CartItemRepository;
import com.example.ecommerce.backend.service.CartService;
import com.example.ecommerce.backend.service.ProductService;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    //TODO: Can Optimize This Function!
    @Override
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        // Validate user and product
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if product is active
        if (!product.getIsActive()) {
            throw new RuntimeException("Product is not available: " + product.getTitle());
        }

        // Check if sufficient stock is available
        if (!productService.canFulfillOrder(productId, quantity)) {
            throw new RuntimeException("Insufficient stock available for product: " + product.getTitle());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            // Update existing item
            CartItem cartItem = existingItem.get();
            Integer newQuantity = cartItem.getQuantity() + quantity;

            // Validate total quantity against stock
            if (!productService.canFulfillOrder(productId, newQuantity)) {
                throw new RuntimeException("Cannot add more items. Insufficient stock available.");
            }

            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    public CartItem updateCartItem(Long userId, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        // Check stock availability
        if (!productService.canFulfillOrder(productId, quantity)) {
            throw new RuntimeException("Insufficient stock available");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.removeItemFromCart(userId, productId);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemRepository.clearUserCart(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findCartItemsByUserIdOrderByCreatedAt(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItem getCartItem(Long userId, Long productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long userId) {
        return cartItemRepository.countCartItemsByUserId(userId).intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalQuantity(Long userId) {
        Integer total = cartItemRepository.getTotalQuantityByUserId(userId);
        return total != null ? total : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(Long userId, Long productId) {
        return cartItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCartForCheckout(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);

        if (cartItems.isEmpty()) {
            return false;
        }

        // Check if all products are still active and in stock
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.getIsActive() || !productService.canFulfillOrder(product.getId(), item.getQuantity())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void removeExpiredCartItems() {
        // Remove cart items older than 30 days
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        cartItemRepository.deleteOldCartItems(cutoffDate);
    }

    @Override
    public void syncCartWithInventory(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);

        for (CartItem item : cartItems) {
            if (!item.getProduct().getIsActive()) {
                // Remove inactive products
                cartItemRepository.delete(item);
            } else if (!productService.canFulfillOrder(item.getProduct().getId(), item.getQuantity())) {
                // Adjust quantity to available stock
                Integer availableStock = item.getProduct().getQtyAvailable();
                if (availableStock > 0) {
                    item.setQuantity(availableStock);
                    cartItemRepository.save(item);
                } else {
                    cartItemRepository.delete(item);
                }
            }
        }
    }
}