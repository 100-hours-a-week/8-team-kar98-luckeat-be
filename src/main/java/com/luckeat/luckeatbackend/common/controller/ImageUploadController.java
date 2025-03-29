package com.luckeat.luckeatbackend.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Operation(summary = "상품 이미지 업로드", description = "상품 이미지를 S3에 업로드합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/products")
    public ResponseEntity<Map<String, String>> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = s3UploadService.uploadImage(file, "products");
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "가게 이미지 업로드", description = "가게 이미지를 S3에 업로드합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/stores")
    public ResponseEntity<Map<String, String>> uploadStoreImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = s3UploadService.uploadImage(file, "stores");
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "리뷰 이미지 업로드", description = "리뷰 이미지를 S3에 업로드합니다", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, String>> uploadReviewImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = s3UploadService.uploadImage(file, "reviews");
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }
} 