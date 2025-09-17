package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.Inventory;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.repository.InventoryRepository;
import com.example.ecommerce.backend.service.InventoryService;
import com.example.ecommerce.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Inventory createInventoryForProduct(Long productId, Integer initialQuantity) {
        if (inventoryExists(productId)) {
            throw new RuntimeException("Inventory already exists for product: " + productId);
        }

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyAvailable(initialQuantity);
        inventory.setQtyReserved(0);
        inventory.setReorderLevel(10); // Default reorder level

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public Inventory updateInventory(Inventory inventory) {
        if (!inventoryRepository.existsById(inventory.getId())) {
            throw new RuntimeException("Inventory not found with id: " + inventory.getId());
        }
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateStock(Long productId, Integer quantity) {
        if (quantity < 0) {
            throw new RuntimeException("Stock quantity cannot be negative");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        inventory.setQtyAvailable(quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory addStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity to add must be positive");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        inventory.setQtyAvailable(inventory.getQtyAvailable() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory reduceStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity to reduce must be positive");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        if (inventory.getQtyAvailable() < quantity) {
            throw new RuntimeException("Insufficient stock available. Available: " +
                    inventory.getQtyAvailable() + ", Requested: " + quantity);
        }

        inventory.setQtyAvailable(inventory.getQtyAvailable() - quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory reserveStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity to reserve must be positive");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        if (!canFulfillOrder(productId, quantity)) {
            throw new RuntimeException("Cannot reserve stock. Insufficient available quantity.");
        }

        inventory.setQtyReserved(inventory.getQtyReserved() + quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory releaseReservedStock(Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity to release must be positive");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        if (inventory.getQtyReserved() < quantity) {
            throw new RuntimeException("Cannot release more than reserved. Reserved: " +
                    inventory.getQtyReserved() + ", Requested: " + quantity);
        }

        inventory.setQtyReserved(inventory.getQtyReserved() - quantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateReorderLevel(Long productId, Integer reorderLevel) {
        if (reorderLevel < 0) {
            throw new RuntimeException("Reorder level cannot be negative");
        }

        Inventory inventory = getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        inventory.setReorderLevel(reorderLevel);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Inventory> getLowStockItems(Pageable pageable) {
        return inventoryRepository.findLowStockItems(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getInStockItems() {
        return inventoryRepository.findInStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getItemsWithStockBelow(Integer threshold) {
        return inventoryRepository.findItemsWithStockBelow(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getItemsWithReservedStock() {
        return inventoryRepository.findItemsWithReservedStock();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canFulfillOrder(Long productId, Integer requiredQuantity) {
        Integer available = getAvailableQuantity(productId);
        return available >= requiredQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableQuantity(Long productId) {
        Integer available = inventoryRepository.getAvailableQuantityByProductId(productId);
        return available != null ? available : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalReservedByProduct(Long productId) {
        Integer reserved = inventoryRepository.getTotalReservedByProductId(productId);
        return reserved != null ? reserved : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getInventoryByWarehouseLocation(String location) {
        return inventoryRepository.findByWarehouseLocation(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllWarehouseLocations() {
        return inventoryRepository.findAllWarehouseLocations();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOutOfStockItems() {
        return inventoryRepository.countOutOfStockItems();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalInventoryQuantity() {
        Long total = inventoryRepository.getTotalInventoryQuantity();
        return total != null ? total : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalReservedQuantity() {
        Long total = inventoryRepository.getTotalReservedQuantity();
        return total != null ? total : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getRecentlyUpdatedInventory(LocalDateTime since) {
        return inventoryRepository.findRecentlyUpdatedInventory(since);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Inventory> getRecentlyUpdatedInventory(Pageable pageable) {
        return inventoryRepository.findRecentlyUpdatedInventory(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getInventorySnapshot(Pageable pageable) {
        return inventoryRepository.getInventorySnapshot(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean inventoryExists(Long productId) {
        return inventoryRepository.existsByProductId(productId);
    }
}