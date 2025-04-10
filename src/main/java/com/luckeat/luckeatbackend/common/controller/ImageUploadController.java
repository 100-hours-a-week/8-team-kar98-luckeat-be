package com.luckeat.luckeatbackend.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.common.dto.PresignedUrlResponse;
import com.luckeat.luckeatbackend.common.service.S3UploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "이미지 업로드 API", description = "이미지 업로드 관련 API 목록")
public class ImageUploadController {

    private final S3UploadService s3UploadService;
    
    @Operation(summary = "상품 이미지 업로드 URL 생성", description = "상품 이미지 업로드를 위한 프리사인드 URL을 생성합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프리사인드 URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/products/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generateProductImageUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {
        PresignedUrlResponse response = s3UploadService.generatePresignedUrl(fileName, contentType, "products");
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "가게 이미지 업로드 URL 생성", description = "가게 이미지 업로드를 위한 프리사인드 URL을 생성합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프리사인드 URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/stores/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generateStoreImageUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {
        PresignedUrlResponse response = s3UploadService.generatePresignedUrl(fileName, contentType, "stores");
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "리뷰 이미지 업로드 URL 생성", description = "리뷰 이미지 업로드를 위한 프리사인드 URL을 생성합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프리사인드 URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/reviews/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generateReviewImageUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType) {
        PresignedUrlResponse response = s3UploadService.generatePresignedUrl(fileName, contentType, "reviews");
        return ResponseEntity.ok(response);
    }
} 