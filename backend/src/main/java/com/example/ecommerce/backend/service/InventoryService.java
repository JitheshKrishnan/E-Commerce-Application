package com.example.ecommerce.backend.service;

import com.example.ecommerce.backend.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryService {

    // Basic CRUD operations
    Inventory createInventoryForProduct(Long productId, Integer initialQuantity);
    Optional<Inventory> getInventoryByProductId(Long productId);
    Inventory updateInventory(Inventory inventory);

    // Stock management
    Inventory updateStock(Long productId, Integer quantity);
    Inventory addStock(Long productId, Integer quantity);
    Inventory reduceStock(Long productId, Integer quantity);
    Inventory reserveStock(Long productId, Integer quantity);
    Inventory releaseReservedStock(Long productId, Integer quantity);
    Inventory updateReorderLevel(Long productId, Integer reorderLevel);

    // Stock queries
    List<Inventory> getLowStockItems();
    Page<Inventory> getLowStockItems(Pageable pageable);
    List<Inventory> getOutOfStockItems();
    List<Inventory> getInStockItems();
    List<Inventory> getItemsWithStockBelow(Integer threshold);
    List<Inventory> getItemsWithReservedStock();

    // Availability checks
    boolean canFulfillOrder(Long productId, Integer requiredQuantity);
    Integer getAvailableQuantity(Long productId);
    Integer getTotalReservedByProduct(Long productId);

    // Warehouse management
    List<Inventory> getInventoryByWarehouseLocation(String location);
    List<String> getAllWarehouseLocations();

    // Analytics and reporting
    Long countLowStockItems();
    Long countOutOfStockItems();
    Long getTotalInventoryQuantity();
    Long getTotalReservedQuantity();
    List<Inventory> getRecentlyUpdatedInventory(LocalDateTime since);
    Page<Inventory> getRecentlyUpdatedInventory(Pageable pageable);
    Page<Object[]> getInventorySnapshot(Pageable pageable);

    // Validation
    boolean inventoryExists(Long productId);
}