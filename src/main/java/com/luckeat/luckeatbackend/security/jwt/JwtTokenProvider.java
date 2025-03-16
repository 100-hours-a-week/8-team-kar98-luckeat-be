package com.luckeat.luckeatbackend.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final Key key;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;

	public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
			@Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
			@Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
	}

	// 액세스 토큰 생성
	public String createAccessToken(Authentication authentication) {
		return createToken(authentication, accessTokenValidityInMilliseconds);
	}

	// 리프레시 토큰 생성
	public String createRefreshToken(Authentication authentication) {
		return createToken(authentication, refreshTokenValidityInMilliseconds);
	}

	// 토큰 생성 메서드
	private String createToken(Authentication authentication, long validityInMilliseconds) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity = new Date(now + validityInMilliseconds);

		return Jwts.builder().setSubject(authentication.getName()).claim("auth", authorities).setIssuedAt(new Date(now))
				.setExpiration(validity).signWith(key, SignatureAlgorithm.HS512).compact();
	}

	// 토큰에서 Authentication 객체 추출
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);

		if (claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		// UserDetails 객체를 만들어서 Authentication 리턴
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			log.error("유효하지 않은 JWT 서명입니다.");
		} catch (MalformedJwtException e) {
			log.error("유효하지 않은 JWT 토큰입니다.");
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 비어있습니다.");
		}
		return false;
	}

	// 토큰에서 클레임 추출
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}