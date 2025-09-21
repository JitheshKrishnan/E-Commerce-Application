package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.dto.ProductResponse;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {

    private final ProductService productService;

    //TODO: Can Incorporate With public/filters in ProductController
    @GetMapping("/products")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> products;

            if (q != null && !q.trim().isEmpty()) {
                // Text search takes precedence
                products = productService.searchProducts(q, pageable);
            } else {
                // Apply filters
                products = productService.getProductsWithFilters(category, brand, minPrice, maxPrice, pageable);
            }

            Page<ProductResponse> response = products.map(ProductResponse::new);

            // Add search metadata
            Map<String, Object> searchResult = Map.of(
                    "results", response,
                    "searchQuery", q != null ? q : "",
                    "appliedFilters", Map.of(
                            "category", category != null ? category : "",
                            "brand", brand != null ? brand : "",
                            "minPrice", minPrice != null ? minPrice : "",
                            "maxPrice", maxPrice != null ? maxPrice : ""
                    ),
                    "totalResults", response.getTotalElements()
            );

            return ResponseEntity.ok(new ApiResponse("Search completed successfully", searchResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Search failed: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Complete Before Deployment
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSearchSuggestions(@RequestParam String q) {
        try {
            if (q == null || q.trim().length() < 2) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Query too short", List.of()));
            }

            // In real implementation, this would:
            // 1. Search in product titles/descriptions
            // 2. Use cached popular searches
            // 3. Include category suggestions
            // 4. Use elasticsearch or similar for better performance

            List<String> suggestions = List.of(
                            q + " laptop",
                            q + " phone",
                            q + " accessories",
                            q + " headphones",
                            q + " deals"
                    ).stream()
                    .filter(suggestion -> !suggestion.equals(q))
                    .limit(5)
                    .toList();

            Map<String, Object> result = Map.of(
                    "query", q,
                    "suggestions", suggestions,
                    "count", suggestions.size()
            );

            return ResponseEntity.ok(new ApiResponse("Suggestions retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get suggestions: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Complete Before Deployment
    @GetMapping("/autocomplete")
    public ResponseEntity<?> getAutocomplete(@RequestParam String q) {
        try {
            if (q == null || q.trim().length() < 2) {
                return ResponseEntity.ok(new ApiResponse("Autocomplete results", List.of()));
            }

            // In real implementation, search product titles that start with query
            List<String> autocomplete = List.of(
                            "iPhone 14",
                            "iPad Pro",
                            "iMac",
                            "AirPods Pro",
                            "Apple Watch"
                    ).stream()
                    .filter(item -> item.toLowerCase().contains(q.toLowerCase()))
                    .limit(8)
                    .toList();

            return ResponseEntity.ok(new ApiResponse("Autocomplete results retrieved", autocomplete));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get autocomplete: " + e.getMessage(), null));
        }
    }

    //TODO: Modify It According to Use Case
    @GetMapping("/filters")
    public ResponseEntity<?> getAvailableFilters() {
        try {
            List<String> categories = productService.getAllActiveCategories();
            List<String> brands = productService.getAllActiveBrands();

            Map<String, Object> filters = Map.of(
                    "categories", categories,
                    "brands", brands,
                    "priceRanges", List.of(
                            Map.of("label", "Under $25", "min", 0, "max", 25),
                            Map.of("label", "$25 - $50", "min", 25, "max", 50),
                            Map.of("label", "$50 - $100", "min", 50, "max", 100),
                            Map.of("label", "$100 - $250", "min", 100, "max", 250),
                            Map.of("label", "$250 - $500", "min", 250, "max", 500),
                            Map.of("label", "Over $500", "min", 500, "max", 999999)
                    ),
                    "sortOptions", List.of(
                            Map.of("label", "Newest First", "value", "createdAt", "direction", "desc"),
                            Map.of("label", "Price: Low to High", "value", "price", "direction", "asc"),
                            Map.of("label", "Price: High to Low", "value", "price", "direction", "desc"),
                            Map.of("label", "Best Selling", "value", "popularity", "direction", "desc"),
                            Map.of("label", "Customer Rating", "value", "rating", "direction", "desc")
                    )
            );

            return ResponseEntity.ok(new ApiResponse("Filters retrieved successfully", filters));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get filters: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Complete Before Deployment
    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingSearches() {
        try {
            // In real implementation, this would come from search analytics
            List<Map<String, Object>> trending = List.of(
                    Map.of("query", "wireless headphones", "searches", 1250),
                    Map.of("query", "smartphone", "searches", 980),
                    Map.of("query", "laptop deals", "searches", 750),
                    Map.of("query", "fitness tracker", "searches", 680),
                    Map.of("query", "gaming mouse", "searches", 520)
            );

            return ResponseEntity.ok(new ApiResponse("Trending searches retrieved", trending));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get trending searches: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Complete Before Deployment
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularProducts(
            @RequestParam(defaultValue = "12") int limit) {
        try {
            // In real implementation, this would be based on sales/views data
            Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
            Page<Product> products = productService.getActiveProducts(pageable);
            List<ProductResponse> response = products.getContent().stream()
                    .map(ProductResponse::new)
                    .toList();

            return ResponseEntity.ok(new ApiResponse("Popular products retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get popular products: " + e.getMessage(), null));
        }
    }

    //TODO: Incorporate Into ProductController
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentProducts(
            @RequestParam(defaultValue = "12") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
            Page<Product> products = productService.getRecentProducts(pageable);
            List<ProductResponse> response = products.getContent().stream()
                    .map(ProductResponse::new)
                    .toList();

            return ResponseEntity.ok(new ApiResponse("Recent products retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get recent products: " + e.getMessage(), null));
        }
    }

    //TODO: Incorporate Into ProductController
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> products = productService.getProductsByCategory(categoryName, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            Map<String, Object> result = Map.of(
                    "category", categoryName,
                    "products", response,
                    "totalProducts", response.getTotalElements()
            );

            return ResponseEntity.ok(new ApiResponse("Category products retrieved", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get category products: " + e.getMessage(), null));
        }
    }

    //TODO: Incorporate Into ProductController
    @GetMapping("/brand/{brandName}")
    public ResponseEntity<?> getProductsByBrand(
            @PathVariable String brandName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> products = productService.getProductsByBrand(brandName, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::new);

            Map<String, Object> result = Map.of(
                    "brand", brandName,
                    "products", response,
                    "totalProducts", response.getTotalElements()
            );

            return ResponseEntity.ok(new ApiResponse("Brand products retrieved", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get brand products: " + e.getMessage(), null));
        }
    }

    //TODO: Incomplete, Complete Before Deployment
    @PostMapping("/save-search")
    public ResponseEntity<?> saveSearch(
            @RequestParam String query,
            @RequestParam(required = false) String filters) {
        try {
            // In real implementation, save search for analytics
            Map<String, Object> savedSearch = Map.of(
                    "query", query,
                    "filters", filters != null ? filters : "",
                    "timestamp", java.time.LocalDateTime.now(),
                    "status", "saved"
            );

            return ResponseEntity.ok(new ApiResponse("Search saved successfully", savedSearch));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to save search: " + e.getMessage(), null));
        }
    }
}