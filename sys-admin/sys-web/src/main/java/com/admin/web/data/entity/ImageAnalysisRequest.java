package com.admin.web.data.entity;

import lombok.Data;

import java.util.List;

@Data
public class ImageAnalysisRequest {
    private String imageUrl;
    private String imageBase64;
    private boolean objectDetection = true;
    private boolean sceneUnderstanding = true;
    private boolean generateDescription = true;
    private Long userId;
}
