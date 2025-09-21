package com.example.ecommerce.backend.controller;

import com.example.ecommerce.backend.dto.ApiResponse;
import com.example.ecommerce.backend.model.Product;
import com.example.ecommerce.backend.repository.ProductRepository;
import com.example.ecommerce.backend.service.impl.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileUploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductRepository productRepository;

    // Allowed image types for free tier optimization
    private final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // Max file size: 10MB (Cloudinary free tier limit)
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping("/upload/product-image/{productId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            String validationError = validateFile(file);
            if (validationError != null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(validationError, null));
            }

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadProductImage(file);

            // Extract URLs
            String publicId = (String) uploadResult.get("public_id");
            String originalUrl = (String) uploadResult.get("secure_url");
            String thumbnailUrl = cloudinaryService.generateThumbnailUrl(publicId);

            // 🔹 Save to DB
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
            product.setImageUrl(originalUrl);
            productRepository.save(product);

            Map<String, Object> response = Map.of(
                    "publicId", publicId,
                    "originalUrl", originalUrl,
                    "thumbnailUrl", thumbnailUrl,
                    "format", uploadResult.get("format"),
                    "width", uploadResult.get("width"),
                    "height", uploadResult.get("height"),
                    "bytes", uploadResult.get("bytes")
            );

            return ResponseEntity.ok(new ApiResponse("Image uploaded & saved successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to upload image: " + e.getMessage(), null));
        }
    }

    @PostMapping("/upload/avatar")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('SUPPORT') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            String validationError = validateFile(file);
            if (validationError != null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(validationError, null));
            }

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadUserAvatar(file);

            // Extract important information
            String publicId = (String) uploadResult.get("public_id");
            String originalUrl = (String) uploadResult.get("secure_url");
            String thumbnailUrl = cloudinaryService.generateOptimizedUrl(publicId, 150, 150);

            Map<String, Object> response = Map.of(
                    "publicId", publicId,
                    "originalUrl", originalUrl,
                    "thumbnailUrl", thumbnailUrl,
                    "format", uploadResult.get("format"),
                    "width", uploadResult.get("width"),
                    "height", uploadResult.get("height"),
                    "bytes", uploadResult.get("bytes")
            );

            return ResponseEntity.ok(new ApiResponse("Avatar uploaded successfully", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to upload avatar: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{publicId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable String publicId) {
        try {
            // Replace URL-safe characters back to original
            String decodedPublicId = publicId.replace("~", "/");

            cloudinaryService.deleteImage(decodedPublicId);

            return ResponseEntity.ok(new ApiResponse("Image deleted successfully",
                    Map.of("deletedPublicId", decodedPublicId)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to delete image: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getImageByProductId(@PathVariable Long productId){
        String imageUrl = productRepository.getImageUrlByProductId(productId);

        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(new ApiResponse("Image Fetched Successfully!", imageUrl));
    }

    @GetMapping("/transform/{publicId}")
    public ResponseEntity<?> getTransformedImage(
            @PathVariable String publicId,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {
        try {
            // Replace URL-safe characters back to original
            String decodedPublicId = publicId.replace("~", "/");

            String transformedUrl = cloudinaryService.generateOptimizedUrl(decodedPublicId, width, height);

            Map<String, Object> response = Map.of(
                    "publicId", decodedPublicId,
                    "transformedUrl", transformedUrl,
                    "width", width,
                    "height", height
            );

            return ResponseEntity.ok(new ApiResponse("Transformed URL generated", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to generate transformed URL: " + e.getMessage(), null));
        }
    }

    @PostMapping("/upload/multiple")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files.length > 10) { // Limit for free tier
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Maximum 10 files allowed per upload", null));
            }

            java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
            java.util.List<String> errors = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    String validationError = validateFile(file);
                    if (validationError != null) {
                        errors.add("File " + file.getOriginalFilename() + ": " + validationError);
                        continue;
                    }

                    Map<String, Object> uploadResult = cloudinaryService.uploadProductImage(file);
                    String publicId = (String) uploadResult.get("public_id");
                    String originalUrl = (String) uploadResult.get("secure_url");
                    String thumbnailUrl = cloudinaryService.generateThumbnailUrl(publicId);

                    results.add(Map.of(
                            "filename", file.getOriginalFilename(),
                            "publicId", publicId,
                            "originalUrl", originalUrl,
                            "thumbnailUrl", thumbnailUrl,
                            "status", "success"
                    ));

                } catch (Exception e) {
                    errors.add("File " + file.getOriginalFilename() + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = Map.of(
                    "successful", results,
                    "errors", errors,
                    "totalFiles", files.length,
                    "successCount", results.size(),
                    "errorCount", errors.size()
            );

            return ResponseEntity.ok(new ApiResponse("Batch upload completed", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to upload images: " + e.getMessage(), null));
        }
    }

    @GetMapping("/usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCloudinaryUsage() {
        try {
            // Note: This would require additional Cloudinary API calls to get usage stats
            // For free tier monitoring
            Map<String, Object> usageInfo = Map.of(
                    "message", "Usage information available in Cloudinary dashboard",
                    "freeLimit", "25GB storage, 25GB monthly bandwidth",
                    "recommendation", "Monitor usage in Cloudinary console"
            );

            return ResponseEntity.ok(new ApiResponse("Usage information", usageInfo));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to get usage: " + e.getMessage(), null));
        }
    }

    // Helper method to validate uploaded files
    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size exceeds 10MB limit";
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return "Only JPEG, PNG, and WebP images are allowed";
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            return "Invalid filename";
        }

        return null; // No validation errors
    }
}