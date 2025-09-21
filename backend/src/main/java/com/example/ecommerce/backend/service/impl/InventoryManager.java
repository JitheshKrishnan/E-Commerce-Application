package com.example.ecommerce.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ecommerce.backend.repository.InventoryRepository;
import com.example.ecommerce.backend.repository.ProductRepository;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.model.Inventory;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryManager {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public void syncProductAndInventoryStock(Long productId, Integer quantity) {
        // Update both product and inventory in sync
        updateProductStock(productId, quantity);
        updateInventoryStock(productId, quantity);
    }

    private void updateProductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQtyAvailable(quantity);
        productRepository.save(product);
    }

    private void updateInventoryStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setQtyAvailable(quantity);
        inventoryRepository.save(inventory);
    }

    public void createInventoryForNewProduct(Long productId, Integer initialQuantity) {
        if (inventoryRepository.existsByProductId(productId)) {
            throw new RuntimeException("Inventory already exists for product: " + productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyAvailable(initialQuantity != null ? initialQuantity : 0);
        inventory.setQtyReserved(0);
        inventory.setReorderLevel(10);

        inventoryRepository.save(inventory);
    }
}