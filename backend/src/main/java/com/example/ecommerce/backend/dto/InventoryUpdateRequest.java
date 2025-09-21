package com.example.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InventoryUpdateRequest {
    private Integer qtyAvailable;
    private Integer reorderLevel;

    @NotBlank(message = "Warehouse location is required")
    private String warehouse;
}
