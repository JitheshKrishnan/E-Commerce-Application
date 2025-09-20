package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Product Response DTOs
@Data
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer qtyAvailable;
    private String category;
    private String brand;
    private String imageUrl;
    private String sku;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.qtyAvailable = product.getQtyAvailable();
        this.category = product.getCategory();
        this.brand = product.getBrand();
        this.imageUrl = product.getImageUrl();
        this.sku = product.getSku();
        this.height = product.getHeight();
        this.weight = product.getWeight();
        this.length = product.getLength();
        this.width = product.getWidth();
        this.isActive = product.getIsActive();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}