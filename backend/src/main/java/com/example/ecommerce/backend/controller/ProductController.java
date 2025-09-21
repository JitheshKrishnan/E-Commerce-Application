package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.ProductRequest;
import com.example.ecommerce.backend.dto.ProductResponse;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private final ProductService productService;

    // Public endpoints - no authentication required
    @GetMapping("/public")
    public ResponseEntity<?> getPublicProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> products = productService.getActiveProducts(pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (!product.getIsActive()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Product not available", null));
            }

            ProductResponse response = new ProductResponse(product);
            return ResponseEntity.ok(new ApiResponse("Product retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get product: " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.searchProducts(q, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            return ResponseEntity.ok(new ApiResponse("Search completed", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Search failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsWithFilters(category, brand, minPrice, maxPrice, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            return ResponseEntity.ok(new ApiResponse("Filter applied successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Filter failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/categories")
    public ResponseEntity<?> getCategories() {
        try {
            List<String> categories = productService.getAllActiveCategories();
            return ResponseEntity.ok(new ApiResponse("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get categories: " + e.getMessage(), null));
        }
    }

    @GetMapping("/public/brands")
    public ResponseEntity<?> getBrands() {
        try {
            List<String> brands = productService.getAllActiveBrands();
            return ResponseEntity.ok(new ApiResponse("Brands retrieved successfully", brands));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get brands: " + e.getMessage(), null));
        }
    }

    // Seller/Admin endpoints
    @GetMapping("/manage")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> getManageableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProducts(pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            return ResponseEntity.ok(new ApiResponse("Products retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get products: " + e.getMessage(), null));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product product = new Product();
            product.setTitle(request.getTitle());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQtyAvailable(request.getQtyAvailable());
            product.setCategory(request.getCategory());
            product.setBrand(request.getBrand());
            product.setImageUrl(request.getImageUrl());
            product.setSku(request.getSku());
            product.setHeight((request.getHeight()));
            product.setWeight((request.getWeight()));
            product.setLength((request.getLength()));
            product.setWidth((request.getWidth()));

            Product savedProduct = productService.createProduct(product);
            ProductResponse response = new ProductResponse(savedProduct);

            return ResponseEntity.ok(new ApiResponse("Product created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create product: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if(request.getTitle() != null) product.setTitle(request.getTitle());
            if(request.getDescription() != null) product.setDescription(request.getDescription());
            if(request.getPrice() != null) product.setPrice(request.getPrice());
            if(request.getQtyAvailable() != null) product.setQtyAvailable(request.getQtyAvailable());
            if(request.getCategory() != null) product.setCategory(request.getCategory());
            if(request.getBrand() != null) product.setBrand(request.getBrand());
            if(request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
            if(request.getHeight() != null) product.setHeight((request.getHeight()));
            if(request.getWeight() != null) product.setWeight((request.getWeight()));
            if(request.getLength() != null) product.setLength((request.getLength()));
            if(request.getWidth() != null) product.setWidth((request.getWidth()));

            Product updatedProduct = productService.updateProduct(product);
            ProductResponse response = new ProductResponse(updatedProduct);

            return ResponseEntity.ok(new ApiResponse("Product updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update product: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            Product product = productService.updateStock(id, quantity);
            ProductResponse response = new ProductResponse(product);

            return ResponseEntity.ok(new ApiResponse("Stock updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update stock: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> activateProduct(@PathVariable Long id) {
        try {
            Product product = productService.activateProduct(id);
            ProductResponse response = new ProductResponse(product);

            return ResponseEntity.ok(new ApiResponse("Product activated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to activate product: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long id) {
        try {
            Product product = productService.deactivateProduct(id);
            ProductResponse response = new ProductResponse(product);

            return ResponseEntity.ok(new ApiResponse("Product deactivated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to deactivate product: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new ApiResponse("Product deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to delete product: " + e.getMessage(), null));
        }
    }
}