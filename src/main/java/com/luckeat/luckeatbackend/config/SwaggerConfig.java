package com.luckeat.luckeatbackend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
                
        // API 정보 설정과 시큐리티 스키마 등록
        return new OpenAPI()
                .info(new Info()
                        .title("LuckEat API")
                        .description("LuckEat 백엔드 API 문서")
                        .version("v1.0.0"))
                .components(new Components().addSecuritySchemes("jwt", jwtScheme))
                .addSecurityItem(new SecurityRequirement().addList("jwt"))
                .servers(List.of(new Server().url("/").description("현재 서버")));
    }
} 