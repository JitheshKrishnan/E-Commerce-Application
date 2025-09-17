package com.example.ecommerce.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

// Product DTOs
@Data
public class ProductRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private java.math.BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer qtyAvailable;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    private String imageUrl;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;
}