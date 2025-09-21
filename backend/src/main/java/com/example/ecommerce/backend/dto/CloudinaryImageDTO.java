package com.example.ecommerce.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryImageDTO {
    private String publicId;
    private String originalUrl;
    private String thumbnailUrl;
    private String largeUrl;
    private String format;
    private Integer width;
    private Integer height;
    private Long bytes;
    private String folder;
}

