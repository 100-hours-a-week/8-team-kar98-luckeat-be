package com.luckeat.luckeatbackend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT 시큐리티 스키마 정의
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        
        // 공통 응답 정의
        Components components = new Components()
                .addSecuritySchemes("jwt", jwtScheme)
                .addResponses("200", new ApiResponse().description("OK - 요청이 성공적으로 처리되었습니다."))
                .addResponses("201", new ApiResponse().description("Created - 새 리소스가 성공적으로 생성되었습니다."))
                .addResponses("400", new ApiResponse().description("Bad Request - 잘못된 요청 또는 유효성 검증 실패"))
                .addResponses("401", new ApiResponse().description("Unauthorized - 인증되지 않은 사용자"))
                .addResponses("403", new ApiResponse().description("Forbidden - 접근 권한이 없습니다."))
                .addResponses("404", new ApiResponse().description("Not Found - 요청한 리소스를 찾을 수 없습니다."))
                .addResponses("409", new ApiResponse().description("Conflict - 리소스 충돌 (예: 중복된 데이터)"))
                .addResponses("500", new ApiResponse().description("Internal Server Error - 서버 오류가 발생했습니다."));
                
        // API 정보 설정과 시큐리티 스키마 등록
        return new OpenAPI()
                .info(new Info()
                        .title("LuckEat API")
                        .description("LuckEat 백엔드 API 문서")
                        .version("v1.0.0"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("jwt"))
                .servers(List.of(new Server().url("/").description("현재 서버")));
    }
} 