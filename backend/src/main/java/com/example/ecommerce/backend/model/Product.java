package com.example.ecommerce.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category"),
                @Index(name = "idx_product_brand", columnList = "brand"),
                @Index(name = "idx_product_price", columnList = "price"),
                @Index(name = "idx_product_active", columnList = "is_active"),
                @Index(name = "idx_product_created", columnList = "created_at"),
                @Index(name = "idx_product_sku", columnList = "sku", unique = true)
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"cartItems", "orderItems", "inventory"})
public class Product {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "qty_available", nullable = false)
    private Integer qtyAvailable = 0;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(length = 100)
    private String category;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    @Column(length = 100)
    private String brand;

    // Unique SKU for inventory management
    @EqualsAndHashCode.Include
    @Column(unique = true, length = 50)
    private String sku;

    // Weight for shipping calculations (in grams)
    @Column(precision = 8, scale = 2)
    private BigDecimal weight;

    // Dimensions for shipping (in cm)
    @Column(precision = 6, scale = 2)
    private BigDecimal length;

    @Column(precision = 6, scale = 2)
    private BigDecimal width;

    @Column(precision = 6, scale = 2)
    private BigDecimal height;

    // JSON field for flexible attributes (MySQL 5.7+ JSON, will become PostgreSQL JSONB)
    @Column(name = "attributes", columnDefinition = "JSON")
    private String attributes;

    // Comma-separated tags for now (will become array in PostgreSQL)
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Bidirectional relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Inventory inventory;

    // Helper methods
    public List<String> getTagsAsList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(tags.split(","));
    }

    public void setTagsFromList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagList);
        }
    }
}