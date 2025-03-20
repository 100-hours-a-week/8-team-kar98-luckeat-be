package com.luckeat.luckeatbackend.users.service;

import org.springframework.stereotype.Service;
import com.luckeat.luckeatbackend.users.repository.JwtBlacklistRepository;
import com.luckeat.luckeatbackend.users.model.JwtBlacklist;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final String secret; // JWT 서명에 사용될 비밀 키

    @Autowired
    public JwtBlacklistService(JwtBlacklistRepository jwtBlacklistRepository, 
                                  @Value("${jwt.secret-key}")  String secret) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.secret = secret; // secret 초기화
    }

    public void addTokenToBlacklist(String token) {
        // 토큰에서 만료 시간 추출
        LocalDateTime expirationDate = extractExpirationDateFromToken(token);
        
        // JwtBlacklist 엔티티 생성 및 저장
        JwtBlacklist jwtBlacklist = JwtBlacklist.builder()
            .token(token)
            .expirationDate(expirationDate)
            .blacklistedAt(LocalDateTime.now())
            .build();
            
        jwtBlacklistRepository.save(jwtBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistRepository.existsByToken(token);
    }

    private LocalDateTime extractExpirationDateFromToken(String token) {
        try {
            // JWT 토큰 파싱
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 만료 시간 추출
            Date expirationDate = claims.getExpiration();
            if (expirationDate == null) {
                // 만료 시간이 없는 경우 현재 시간으로부터 1시간 후로 설정
                return LocalDateTime.now().plusHours(1);
            }
            
            // Date를 LocalDateTime으로 변환
            return Instant.ofEpochMilli(expirationDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            // 토큰 파싱 실패 시 현재 시간으로부터 1시간 후로 설정
            return LocalDateTime.now().plusHours(1);
        }
    }
} 