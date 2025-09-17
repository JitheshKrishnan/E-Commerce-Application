package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    // Basic CRUD operations
    Product createProduct(Product product);
    Optional<Product> getProductById(Long id);
    Optional<Product> getProductBySku(String sku);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
    List<Product> getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);

    // Product management
    Product activateProduct(Long productId);
    Product deactivateProduct(Long productId);
    List<Product> getActiveProducts();
    Page<Product> getActiveProducts(Pageable pageable);

    // Inventory management
    Product updateStock(Long productId, Integer quantity);
    boolean isInStock(Long productId);
    boolean canFulfillOrder(Long productId, Integer requiredQuantity);
    List<Product> getLowStockProducts(Integer threshold);
    List<Product> getOutOfStockProducts();

    // Search and filter
    Page<Product> searchProducts(String searchTerm, Pageable pageable);
    Page<Product> getProductsByCategory(String category, Pageable pageable);
    Page<Product> getProductsByBrand(String brand, Pageable pageable);
    Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Product> getProductsWithFilters(String category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Categories and brands
    List<String> getAllActiveCategories();
    List<String> getAllActiveBrands();

    // Recent and featured products
    Page<Product> getRecentProducts(Pageable pageable);
    List<Product> getProductsCreatedSince(LocalDateTime since);

    // Statistics
    Long countActiveProducts();
    BigDecimal getAveragePrice();

    // Validation
    boolean skuExists(String sku);
    boolean isValidProduct(Long productId);
}