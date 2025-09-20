package com.example.ecommerce.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

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

    @DecimalMin(value = "0.0", inclusive = false, message = "Height must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Height must be a valid number with up to 6 digits and 2 decimals")
    private BigDecimal height;

    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Weight must be a valid number with up to 6 digits and 2 decimals")
    private BigDecimal weight;

    @DecimalMin(value = "0.0", inclusive = false, message = "Length must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Length must be a valid number with up to 6 digits and 2 decimals")
    private BigDecimal length;

    @DecimalMin(value = "0.0", inclusive = false, message = "Width must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Width must be a valid number with up to 6 digits and 2 decimals")
    private BigDecimal width;

}