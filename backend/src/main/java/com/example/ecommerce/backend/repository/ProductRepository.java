package com.example.ecommerce.backend.repository;

import com.example.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Basic queries
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByIsActive(Boolean isActive);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    // Pagination queries
    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByBrand(String brand, Pageable pageable);

    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Price range queries
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.isActive = true")
    Page<Product> findActiveProductsByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // Stock queries
    @Query("SELECT p FROM Product p WHERE p.qtyAvailable > 0 AND p.isActive = true")
    Page<Product> findInStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.qtyAvailable = 0 AND p.isActive = true")
    List<Product> findOutOfStockProducts();

    @Query("SELECT p FROM Product p WHERE p.qtyAvailable <= :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // Category and Brand aggregations
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true ORDER BY p.category")
    List<String> findAllActiveCategories();

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true ORDER BY p.brand")
    List<String> findAllActiveBrands();

    // Search functionality
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (" +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchActiveProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Complex filters
    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "p.isActive = true")
    Page<Product> findProductsWithFilters(
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // Statistics
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    Long countActiveProducts();

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.isActive = true")
    BigDecimal getAveragePrice();

    // Recent products
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<Product> findRecentProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.createdAt >= :since AND p.isActive = true")
    List<Product> findProductsCreatedSince(@Param("since") LocalDateTime since);

    // Update operations
    @Modifying
    @Query("UPDATE Product p SET p.qtyAvailable = :quantity WHERE p.id = :productId")
    int updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = false WHERE p.id = :productId")
    int deactivateProduct(@Param("productId") Long productId);
}