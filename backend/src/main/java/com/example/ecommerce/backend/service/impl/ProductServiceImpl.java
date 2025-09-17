package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.repository.ProductRepository;
import com.example.ecommerce.backend.service.InventoryService;
import com.example.ecommerce.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public Product createProduct(Product product) {
        // Generate SKU if not provided
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            product.setSku(generateUniqueSku());
        }

        if (skuExists(product.getSku())) {
            throw new RuntimeException("SKU already exists: " + product.getSku());
        }

        Product savedProduct = productRepository.save(product);

        // Create corresponding inventory entry
        inventoryService.createInventoryForProduct(savedProduct.getId(), product.getQtyAvailable());

        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new RuntimeException("Product not found with id: " + product.getId());
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product activateProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setIsActive(true);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public Product deactivateProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setIsActive(false);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByIsActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActive(true, pageable);
    }

    @Override
    public Product updateStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setQtyAvailable(quantity);
            Product updatedProduct = productRepository.save(product);

            // Update inventory as well
            inventoryService.updateStock(productId, quantity);

            return updatedProduct;
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.isPresent() && productOpt.get().getQtyAvailable() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canFulfillOrder(Long productId, Integer requiredQuantity) {
        return inventoryService.canFulfillOrder(productId, requiredQuantity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.searchActiveProducts(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findActiveProductsByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsWithFilters(String category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findProductsWithFilters(category, brand, minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllActiveCategories() {
        return productRepository.findAllActiveCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllActiveBrands() {
        return productRepository.findAllActiveBrands();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getRecentProducts(Pageable pageable) {
        return productRepository.findRecentProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsCreatedSince(LocalDateTime since) {
        return productRepository.findProductsCreatedSince(since);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveProducts() {
        return productRepository.countActiveProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAveragePrice() {
        return productRepository.getAveragePrice();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean skuExists(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidProduct(Long productId) {
        return productRepository.existsById(productId);
    }

    private String generateUniqueSku() {
        String sku;
        do {
            sku = "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (skuExists(sku));
        return sku;
    }
}