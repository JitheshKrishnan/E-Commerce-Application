package com.example.ecommerce.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "quality", "auto:eco", // Optimize quality for free tier
                "fetch_format", "auto", // Automatic format optimization
                "flags", "progressive" // Progressive JPEG for better loading
        ));
    }

    public Map<String, Object> uploadProductImage(MultipartFile file) throws IOException {
        return uploadImage(file, "ecommerce/products");
    }

    public Map<String, Object> uploadUserAvatar(MultipartFile file) throws IOException {
        return uploadImage(file, "ecommerce/avatars");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String generateOptimizedUrl(String publicId, int width, int height) {
        return cloudinary.url()
                .transformation(new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .quality("auto:eco")
                        .fetchFormat("auto"))
                .generate(publicId);    
    }

    public String generateThumbnailUrl(String publicId) {
        return generateOptimizedUrl(publicId, 300, 300);
    }

    public String generateLargeUrl(String publicId) {
        return generateOptimizedUrl(publicId, 800, 800);
    }
}