package com.example.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Order DTOs
@Data
public class CreateOrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}