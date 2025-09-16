package com.example.ecommerce.backend.repository;

import com.example.ecommerce.backend.model.CartItem;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Basic queries
    List<CartItem> findByUser(User user);

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserAndProduct(User user, Product product);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Cart management
    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId ORDER BY ci.createdAt DESC")
    List<CartItem> findCartItemsByUserIdOrderByCreatedAt(@Param("userId") Long userId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user.id = :userId")
    Long countCartItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user.id = :userId")
    Integer getTotalQuantityByUserId(@Param("userId") Long userId);

    // Product-based queries
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.product.id = :productId")
    Long countCartItemsByProductId(@Param("productId") Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.product.id = :productId")
    List<CartItem> findByProductId(@Param("productId") Long productId);

    // Cleanup operations
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    int clearUserCart(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    int removeItemFromCart(@Param("userId") Long userId, @Param("productId") Long productId);

    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.user.id = :userId AND ci.product.id = :productId")
    int updateCartItemQuantity(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

    // Time-based queries
    @Query("SELECT ci FROM CartItem ci WHERE ci.createdAt < :cutoffDate")
    List<CartItem> findOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.createdAt < :cutoffDate")
    int deleteOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Analytics
    @Query("SELECT ci.product.id, COUNT(ci) FROM CartItem ci GROUP BY ci.product.id ORDER BY COUNT(ci) DESC")
    List<Object[]> findMostAddedToCartProducts();
}