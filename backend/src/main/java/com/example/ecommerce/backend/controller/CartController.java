package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.AddToCartRequest;
import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.CartItemResponse;
import com.example.ecommerce.backend.dto.CartSummaryResponse;
import com.example.ecommerce.backend.model.CartItem;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.service.CartService;
import com.example.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getCart(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartService.getCartItems(user.getId());
            List<CartItemResponse> itemResponses = cartItems.stream()
                    .map(CartItemResponse::new)
                    .collect(Collectors.toList());

            BigDecimal total = cartService.getCartTotal(user.getId());
            Integer itemCount = cartService.getCartItemCount(user.getId());

            CartSummaryResponse response = new CartSummaryResponse(itemResponses, total, itemCount);
            return ResponseEntity.ok(new ApiResponse("Cart retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get cart: " + e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CartItem cartItem = cartService.addToCart(user.getId(), request.getProductId(), request.getQuantity());
            CartItemResponse response = new CartItemResponse(cartItem);

            return ResponseEntity.ok(new ApiResponse("Item added to cart successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to add item to cart: " + e.getMessage(), null));
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long productId, @RequestParam Integer quantity, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CartItem cartItem = cartService.updateCartItem(user.getId(), productId, quantity);

            if (cartItem == null) {
                return ResponseEntity.ok(new ApiResponse("Item removed from cart", null));
            }

            CartItemResponse response = new CartItemResponse(cartItem);
            return ResponseEntity.ok(new ApiResponse("Cart item updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update cart item: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            cartService.removeFromCart(user.getId(), productId);
            return ResponseEntity.ok(new ApiResponse("Item removed from cart successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to remove item from cart: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            cartService.clearCart(user.getId());
            return ResponseEntity.ok(new ApiResponse("Cart cleared successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to clear cart: " + e.getMessage(), null));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Integer count = cartService.getCartItemCount(user.getId());
            return ResponseEntity.ok(new ApiResponse("Cart count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get cart count: " + e.getMessage(), null));
        }
    }


    //TODO: Ensure If Checkout Should be Done After Checking Inventory
    @PostMapping("/validate")
    public ResponseEntity<?> validateCart(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isValid = cartService.validateCartForCheckout(user.getId());

            if (isValid) {
                return ResponseEntity.ok(new ApiResponse("Cart is valid for checkout", true));
            } else {
                // Sync cart with inventory to fix issues
                cartService.syncCartWithInventory(user.getId());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Cart validation failed. Some items were updated or removed.", false));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Cart validation failed: " + e.getMessage(), null));
        }
    }
}