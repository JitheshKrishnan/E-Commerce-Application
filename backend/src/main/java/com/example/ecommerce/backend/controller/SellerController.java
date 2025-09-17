package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.ProductRequest;
import com.example.ecommerce.backend.dto.ProductResponse;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.service.OrderItemService;
import com.example.ecommerce.backend.service.ProductService;
import com.example.ecommerce.backend.service.UserService;
import com.example.ecommerce.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
public class SellerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getSellerDashboard(Authentication auth) {
        try {
            User user = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // In real implementation, filter by seller's products
            Long totalProducts = productService.countActiveProducts();
            List<Product> lowStockProducts = productService.getLowStockProducts(10);
            List<Product> recentProducts = productService.getProductsCreatedSince(LocalDateTime.now().minusDays(7));

            Map<String, Object> dashboard = Map.of(
                    "sellerId", user.getId(),
                    "sellerName", user.getName(),
                    "totalProducts", totalProducts,
                    "lowStockProductsCount", lowStockProducts.size(),
                    "recentProductsCount", recentProducts.size(),
                    "totalInventoryValue", "0.00", // Calculate based on seller's products
                    "pendingOrders", 0, // Count seller's pending orders
                    "todaysSales", "0.00" // Calculate today's sales for seller
            );

            return ResponseEntity.ok(new ApiResponse("Dashboard data retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get dashboard data: " + e.getMessage(), null));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getSellerProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication auth) {
        try {
            // In real implementation, filter by seller's products
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Product> products = productService.getAllProducts(pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            return ResponseEntity.ok(new ApiResponse("Seller products retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get seller products: " + e.getMessage(), null));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> createSellerProduct(@Valid @RequestBody ProductRequest request, Authentication auth) {
        try {
            User seller = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Seller not found"));

            Product product = new Product();
            product.setTitle(request.getTitle());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQtyAvailable(request.getQtyAvailable());
            product.setCategory(request.getCategory());
            product.setBrand(request.getBrand());
            product.setImageUrl(request.getImageUrl());
            product.setSku(request.getSku());
            // In real implementation: product.setSeller(seller);

            Product savedProduct = productService.createProduct(product);
            ProductResponse response = new ProductResponse(savedProduct);

            return ResponseEntity.ok(new ApiResponse("Product created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to create product: " + e.getMessage(), null));
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateSellerProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request,
            Authentication auth) {
        try {
            // In real implementation, verify seller owns this product
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setTitle(request.getTitle());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setQtyAvailable(request.getQtyAvailable());
            product.setCategory(request.getCategory());
            product.setBrand(request.getBrand());
            product.setImageUrl(request.getImageUrl());

            Product updatedProduct = productService.updateProduct(product);
            ProductResponse response = new ProductResponse(updatedProduct);

            return ResponseEntity.ok(new ApiResponse("Product updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update product: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteSellerProduct(@PathVariable Long productId, Authentication auth) {
        try {
            // In real implementation, verify seller owns this product
            productService.deactivateProduct(productId);
            return ResponseEntity.ok(new ApiResponse("Product deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to deactivate product: " + e.getMessage(), null));
        }
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<?> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold,
            Authentication auth) {
        try {
            // In real implementation, filter by seller's products
            List<Product> lowStockProducts = productService.getLowStockProducts(threshold);
            List<ProductResponse> response = lowStockProducts.stream()
                    .map(ProductResponse::new)
                    .toList();

            return ResponseEntity.ok(new ApiResponse("Low stock products retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get low stock products: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getSellerAnalytics(
            @RequestParam(defaultValue = "30") int days,
            Authentication auth) {
        try {
            // In real implementation, filter by seller's products
            List<Object[]> bestSelling = orderItemService.getBestSellingProducts(
                    PageRequest.of(0, 10)
            ).getContent();

            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            List<Object[]> recentSales = orderItemService.getBestSellingProductsBetweenDates(
                    startDate, LocalDateTime.now(), PageRequest.of(0, 10)
            ).getContent();

            Map<String, Object> analytics = Map.of(
                    "period", days + " days",
                    "bestSellingProducts", bestSelling,
                    "recentSales", recentSales,
                    "totalProductsSold", "0", // Calculate for seller
                    "totalRevenue", "0.00" // Calculate for seller
            );

            return ResponseEntity.ok(new ApiResponse("Seller analytics retrieved successfully", analytics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get seller analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        try {
            // In real implementation, get orders containing seller's products
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            // Placeholder - implement actual seller order filtering
            Map<String, Object> orders = Map.of(
                    "content", List.of(),
                    "totalElements", 0,
                    "totalPages", 0,
                    "message", "Seller order filtering to be implemented"
            );

            return ResponseEntity.ok(new ApiResponse("Seller orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get seller orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/inventory")
    public ResponseEntity<?> getSellerInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        try {
            // In real implementation, filter inventory by seller's products
            Pageable pageable = PageRequest.of(page, size);

            Map<String, Object> inventoryStats = Map.of(
                    "totalItems", inventoryService.getTotalInventoryQuantity(),
                    "lowStockCount", inventoryService.countLowStockItems(),
                    "outOfStockCount", inventoryService.countOutOfStockItems(),
                    "message", "Seller inventory filtering to be implemented"
            );

            return ResponseEntity.ok(new ApiResponse("Seller inventory retrieved successfully", inventoryStats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get seller inventory: " + e.getMessage(), null));
        }
    }

    @GetMapping("/sales-report")
    public ResponseEntity<?> getSalesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication auth) {
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

            // In real implementation, calculate actual seller sales
            Map<String, Object> salesReport = Map.of(
                    "period", Map.of("start", start, "end", end),
                    "totalSales", "0.00",
                    "totalOrders", 0,
                    "averageOrderValue", "0.00",
                    "topSellingProducts", List.of(),
                    "message", "Seller-specific sales calculation to be implemented"
            );

            return ResponseEntity.ok(new ApiResponse("Sales report generated successfully", salesReport));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to generate sales report: " + e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getSellerProfile(Authentication auth) {
        try {
            User seller = userService.getUserByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Seller not found"));

            Map<String, Object> profile = Map.of(
                    "id", seller.getId(),
                    "name", seller.getName(),
                    "email", seller.getEmail(),
                    "phone", seller.getPhoneNumber() != null ? seller.getPhoneNumber() : "",
                    "address", seller.getAddress() != null ? seller.getAddress() : "",
                    "joinedDate", seller.getCreatedAt(),
                    "isActive", seller.getIsActive()
            );

            return ResponseEntity.ok(new ApiResponse("Seller profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get seller profile: " + e.getMessage(), null));
        }
    }
}