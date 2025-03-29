package com.luckeat.luckeatbackend.common.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.luckeat.luckeatbackend.common.exception.base.FileUploadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {
    
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.image-path}")
    private String imagePath;
    
    @Value("${aws.region}")
    private String region;
    
    /**
     * 이미지 파일을 S3에 업로드합니다.
     * 
     * @param file 업로드할 이미지 파일
     * @param directory 저장할 디렉토리 (products, stores, reviews)
     * @return 업로드된 이미지의 URL
     */
    public String uploadImage(MultipartFile file, String directory) {
        try {
            validateFile(file);
            String fileName = generateFileName(file.getOriginalFilename());
            String fileKey = String.format("%s/%s/%s", imagePath, directory, fileName);
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileKey);
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("빈 파일은 업로드할 수 없습니다");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileUploadException("이미지 파일만 업로드 가능합니다");
        }
        
        // 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileUploadException("파일 크기는 10MB를 초과할 수 없습니다");
        }
    }
    
    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "-" + originalFilename.replaceAll("\\s", "_");
    }
} 