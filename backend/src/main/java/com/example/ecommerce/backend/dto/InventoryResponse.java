package com.example.ecommerce.backend.dto;

import com.example.ecommerce.backend.model.Inventory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Inventory Response DTOs
@Data
@NoArgsConstructor
public class InventoryResponse {
    private Long id;
    private ProductResponse product;
    private Integer qtyAvailable;
    private Integer qtyReserved;
    private Integer availableForSale;
    private Integer reorderLevel;
    private Boolean isLowStock;
    private String warehouseLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InventoryResponse(Inventory inventory) {
        this.id = inventory.getId();
        this.product = new ProductResponse(inventory.getProduct());
        this.qtyAvailable = inventory.getQtyAvailable();
        this.qtyReserved = inventory.getQtyReserved();
        this.availableForSale = inventory.getAvailableForSale();
        this.reorderLevel = inventory.getReorderLevel();
        this.isLowStock = inventory.isLowStock();
        this.warehouseLocation = inventory.getWarehouseLocation();
        this.createdAt = inventory.getCreatedAt();
        this.updatedAt = inventory.getUpdatedAt();
    }
}