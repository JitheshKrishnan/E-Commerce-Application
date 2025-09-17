package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.InventoryResponse;
import com.example.ecommerce.backend.dto.UpdateStockRequest;
import com.example.ecommerce.backend.model.Inventory;
import com.example.ecommerce.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Inventory> inventories = inventoryService.getRecentlyUpdatedInventory(pageable);
            Page<InventoryResponse> response = inventories.map(InventoryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Inventory retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get inventory: " + e.getMessage(), null));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductInventory(@PathVariable Long productId) {
        try {
            Inventory inventory = inventoryService.getInventoryByProductId(productId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product"));

            InventoryResponse response = new InventoryResponse(inventory);
            return ResponseEntity.ok(new ApiResponse("Inventory retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get inventory: " + e.getMessage(), null));
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("qtyAvailable").ascending());
            Page<Inventory> inventories = inventoryService.getLowStockItems(pageable);
            Page<InventoryResponse> response = inventories.map(InventoryResponse::new);

            return ResponseEntity.ok(new ApiResponse("Low stock items retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get low stock items: " + e.getMessage(), null));
        }
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<?> getOutOfStockItems() {
        try {
            List<Inventory> inventories = inventoryService.getOutOfStockItems();
            List<InventoryResponse> response = inventories.stream()
                    .map(InventoryResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse("Out of stock items retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get out of stock items: " + e.getMessage(), null));
        }
    }

    @GetMapping("/critical-stock")
    public ResponseEntity<?> getCriticalStockItems(@RequestParam(defaultValue = "5") Integer threshold) {
        try {
            List<Inventory> inventories = inventoryService.getItemsWithStockBelow(threshold);
            List<InventoryResponse> response = inventories.stream()
                    .map(InventoryResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse("Critical stock items retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get critical stock items: " + e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            Inventory inventory = inventoryService.updateStock(productId, quantity);
            InventoryResponse response = new InventoryResponse(inventory);

            return ResponseEntity.ok(new ApiResponse("Stock updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update stock: " + e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/add-stock")
    public ResponseEntity<?> addStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Quantity must be positive", null));
            }

            Inventory inventory = inventoryService.addStock(productId, quantity);
            InventoryResponse response = new InventoryResponse(inventory);

            return ResponseEntity.ok(new ApiResponse("Stock added successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to add stock: " + e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/reduce-stock")
    public ResponseEntity<?> reduceStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        try {
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Quantity must be positive", null));
            }

            Inventory inventory = inventoryService.reduceStock(productId, quantity);
            InventoryResponse response = new InventoryResponse(inventory);

            return ResponseEntity.ok(new ApiResponse("Stock reduced successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to reduce stock: " + e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/reorder-level")
    public ResponseEntity<?> updateReorderLevel(@PathVariable Long productId, @RequestParam Integer reorderLevel) {
        try {
            if (reorderLevel < 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Reorder level cannot be negative", null));
            }

            Inventory inventory = inventoryService.updateReorderLevel(productId, reorderLevel);
            InventoryResponse response = new InventoryResponse(inventory);

            return ResponseEntity.ok(new ApiResponse("Reorder level updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update reorder level: " + e.getMessage(), null));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getInventoryStats() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalItems", inventoryService.getTotalInventoryQuantity());
            stats.put("lowStockCount", inventoryService.countLowStockItems());
            stats.put("outOfStockCount", inventoryService.countOutOfStockItems());
            stats.put("totalReserved", inventoryService.getTotalReservedQuantity());
            stats.put("totalAvailable", inventoryService.getTotalInventoryQuantity() - inventoryService.getTotalReservedQuantity());

            return ResponseEntity.ok(new ApiResponse("Inventory stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get inventory stats: " + e.getMessage(), null));
        }
    }

    @GetMapping("/warehouse/{location}")
    public ResponseEntity<?> getInventoryByWarehouse(@PathVariable String location) {
        try {
            List<Inventory> inventories = inventoryService.getInventoryByWarehouseLocation(location);
            List<InventoryResponse> response = inventories.stream()
                    .map(InventoryResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse("Warehouse inventory retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get warehouse inventory: " + e.getMessage(), null));
        }
    }

    @GetMapping("/warehouses")
    public ResponseEntity<?> getWarehouseLocations() {
        try {
            List<String> locations = inventoryService.getAllWarehouseLocations();
            return ResponseEntity.ok(new ApiResponse("Warehouse locations retrieved successfully", locations));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get warehouse locations: " + e.getMessage(), null));
        }
    }

    @GetMapping("/recent-updates")
    public ResponseEntity<?> getRecentInventoryUpdates(
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            List<Inventory> inventories = inventoryService.getRecentlyUpdatedInventory(since);

            // Apply pagination manually since we're filtering by time
            int start = Math.min(page * size, inventories.size());
            int end = Math.min(start + size, inventories.size());
            List<Inventory> pageContent = inventories.subList(start, end);

            List<InventoryResponse> response = pageContent.stream()
                    .map(InventoryResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse("Recent inventory updates retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get recent updates: " + e.getMessage(), null));
        }
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkUpdateInventory(@RequestBody List<UpdateStockRequest> updates) {
        try {
            java.util.Map<String, Object> results = new java.util.HashMap<>();
            int successful = 0;
            int failed = 0;

            for (UpdateStockRequest update : updates) {
                try {
                    inventoryService.updateStock(update.getProductId(), update.getQuantity());
                    successful++;
                } catch (Exception e) {
                    failed++;
                }
            }

            results.put("successful", successful);
            results.put("failed", failed);
            results.put("total", updates.size());

            return ResponseEntity.ok(new ApiResponse("Bulk inventory update completed", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Bulk update failed: " + e.getMessage(), null));
        }
    }
}