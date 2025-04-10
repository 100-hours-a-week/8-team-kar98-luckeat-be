package com.luckeat.luckeatbackend.common.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.luckeat.luckeatbackend.common.dto.PresignedUrlResponse;
import com.luckeat.luckeatbackend.common.exception.base.FileUploadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

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
    
    @Value("${aws.credentials.access-key}")
    private String accessKey;
    
    @Value("${aws.credentials.secret-key}")
    private String secretKey;
    
    /**
     * 이미지 업로드를 위한 프리사인드 URL을 생성합니다.
     * 
     * @param originalFilename 원본 파일명
     * @param contentType 파일 타입
     * @param directory 저장할 디렉토리 (products, stores, reviews)
     * @return 프리사인드 URL과 파일 키를 포함한 객체
     */
    public PresignedUrlResponse generatePresignedUrl(String originalFilename, String contentType, String directory) {
        try {
            validateFileType(contentType);
            String fileName = generateFileName(originalFilename);
            String fileKey = String.format("%s/%s/%s", imagePath, directory, fileName);
            
            try (S3Presigner presigner = S3Presigner.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                    System.getenv("AWS_ACCESS_KEY_ID") != null ? System.getenv("AWS_ACCESS_KEY_ID") : accessKey,
                                    System.getenv("AWS_SECRET_ACCESS_KEY") != null ? System.getenv("AWS_SECRET_ACCESS_KEY") : secretKey
                            )))
                    .build()) {
                
                PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType(contentType);
                
                // ACL이 허용된 경우에만 설정
                boolean useAcl = System.getenv("USE_S3_ACL") == null || Boolean.parseBoolean(System.getenv("USE_S3_ACL"));
                if (useAcl) {
                    requestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
                }
                
                PutObjectRequest objectRequest = requestBuilder.build();
                
                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(objectRequest)
                        .build();
                
                PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
                String presignedUrl = presignedRequest.url().toString();
                
                log.info("프리사인드 URL 생성 성공: fileKey={}, contentType={}", fileKey, contentType);
                
                return PresignedUrlResponse.builder()
                        .presignedUrl(presignedUrl)
                        .fileKey(fileKey)
                        .build();
            }
        } catch (IllegalArgumentException e) {
            log.error("프리사인드 URL 생성 파라미터 오류: {}", e.getMessage());
            throw new FileUploadException("잘못된 파라미터로 인한 URL 생성 실패: " + e.getMessage());
        } catch (SdkClientException e) {
            log.error("AWS SDK 클라이언트 오류: {}", e.getMessage());
            throw new FileUploadException("AWS S3 연결 또는 인증 오류: " + e.getMessage());
        } catch (Exception e) {
            log.error("프리사인드 URL 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new FileUploadException("프리사인드 URL 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void validateFileType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("유효하지 않은 파일 타입: {}", contentType);
            throw new FileUploadException("이미지 파일만 업로드 가능합니다 (제공된 타입: " + contentType + ")");
        }
    }
    
    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "-" + originalFilename.replaceAll("\\s", "_");
    }
} 