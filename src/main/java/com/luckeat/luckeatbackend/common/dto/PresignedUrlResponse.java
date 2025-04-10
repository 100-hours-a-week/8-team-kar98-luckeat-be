package com.luckeat.luckeatbackend.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlResponse {
    private String presignedUrl;
    private String fileKey;
} 