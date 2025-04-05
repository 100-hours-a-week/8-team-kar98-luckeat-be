package com.luckeat.luckeatbackend.users.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.user.EmailDuplicateException;
import com.luckeat.luckeatbackend.common.exception.user.NicknameDuplicateException;
import com.luckeat.luckeatbackend.common.exception.user.PasswordMismatchException;
import com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException;
import com.luckeat.luckeatbackend.common.exception.user.UserInvalidPasswordException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.security.jwt.JwtTokenProvider;
import com.luckeat.luckeatbackend.users.dto.LoginRequestDto;
import com.luckeat.luckeatbackend.users.dto.LoginResponseDto;
import com.luckeat.luckeatbackend.users.dto.NicknameUpdateDto;
import com.luckeat.luckeatbackend.users.dto.PasswordUpdateDto;
import com.luckeat.luckeatbackend.users.dto.RegisterRequestDto;
import com.luckeat.luckeatbackend.users.dto.UserInfoResponseDto;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public List<User> getAllUsers() {
		return userRepository.findByDeletedAtIsNull();
	}

	public Optional<User> getUserById(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId);
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmailAndDeletedAtIsNull(email);
	}

	public Optional<User> getUserByNickname(String nickname) {
		return userRepository.findByNicknameAndDeletedAtIsNull(nickname);
	}


	// 기존 메소드도 유지 (하위 호환성을 위해)
	@Transactional
	public User createUser(RegisterRequestDto registerDto) {
		// 이메일 중복 검사 - 소프트 삭제된 사용자의 이메일도 중복 체크에 포함
		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new EmailDuplicateException();
		}

		// 닉네임 중복 검사 - 소프트 삭제된 사용자의 닉네임도 중복 체크에 포함
		if (userRepository.existsByNickname(registerDto.getNickname())) {
			throw new NicknameDuplicateException();
		}
		
		// Base64로 인코딩된 비밀번호 디코딩
		String decodedPassword = new String(java.util.Base64.getDecoder().decode(registerDto.getPassword()));
		
		// 비밀번호를 소문자로 변환
		String normalizedPassword = decodedPassword.toLowerCase();
		
		// 비밀번호 암호화
		User user = User.builder()
				.email(registerDto.getEmail())
				.nickname(registerDto.getNickname())
				.password(passwordEncoder.encode(normalizedPassword))
				.role(User.Role.valueOf(registerDto.getRole().name()))
				.build();
		
		return userRepository.save(user);
	}

	// 닉네임만 수정하는 메소드
	@Transactional
	public User updateNickname(NicknameUpdateDto nicknameDto) {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException());
		String newNickname = nicknameDto.getNickname();

		// 새 닉네임이 이미 사용 중인지 확인
		if (userRepository.existsByNicknameAndDeletedAtIsNull(newNickname) && !user.getNickname().equals(newNickname)) {
			throw new NicknameDuplicateException();
		}

		user.setNickname(newNickname);
		return userRepository.save(user);
	}

	// 비밀번호 수정 메소드
	@Transactional
	public User updatePassword(PasswordUpdateDto passwordDto) {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException());

		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
			throw new PasswordMismatchException();
		}

		// 새 비밀번호와 확인 비밀번호가 일치하는지 확인
		if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
			throw new PasswordMismatchException();
		}
		
		// 현재 비밀번호와 새 비밀번호가 같은지 확인
		if (passwordEncoder.matches(passwordDto.getNewPassword(), user.getPassword())) {
			throw new UserInvalidPasswordException();
		}

		// 새 비밀번호 암호화 후 저장
		user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
		return userRepository.save(user);
	}

	// JWT 인증 기반 소프트 삭제 구현
	@Transactional
	public void deleteCurrentUser() {
		// 현재 인증된 사용자의 ID 가져오기
		Long userId = getCurrentUserId();

		// 사용자 조회
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException());

		// 소프트 삭제 - 물리적 삭제 대신 deletedAt 설정
		user.setDeletedAt(java.time.LocalDateTime.now());

		// 변경사항 저장
		userRepository.save(user);
	}

	public boolean existsByEmail(String email) {
    // 소프트 삭제된 사용자를 포함하여 이메일 존재 여부 확인
    return userRepository.existsByEmail(email);
	}

	public boolean existsByNickname(String nickname) {
		// 소프트 삭제된 사용자를 포함하여 닉네임 존재 여부 확인
		return userRepository.existsByNickname(nickname);
	}

	// 사용자 로그인 처리
	@Transactional
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		// 이메일로 사용자 조회
		User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequestDto.getEmail())
            .orElseThrow(() -> new UserNotFoundException());
    
		// Base64로 인코딩된 비밀번호 디코딩
		String decodedPassword = new String(java.util.Base64.getDecoder().decode(loginRequestDto.getPassword()));
		
		// 비밀번호를 소문자로 변환
		String normalizedPassword = decodedPassword.toLowerCase();
		
		// 비밀번호 검증
		if (!passwordEncoder.matches(normalizedPassword, user.getPassword())) {
			throw new PasswordMismatchException();
		}

		// 인증 객체 생성
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null,
				Collections.singleton(authority));

		// JWT 토큰 발급
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

		// 응답 DTO 구성
		return LoginResponseDto.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.role(user.getRole().name())
				.grantType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	// 현재 인증된 사용자 ID 가져오기
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
    log.error("인증되지 않은 사용자 접근: {}", authentication);
    throw new UnauthenticatedException();
}

		// 서비스가 아닌 리포지토리를 직접 호출
		String email = authentication.getName();
		return userRepository.findByEmailAndDeletedAtIsNull(email)
				.orElseThrow(() -> new UserNotFoundException()).getId();
	}

	// 현재 인증된 사용자 정보 조회 - DTO 반환 버전
	public UserInfoResponseDto getCurrentUserInfo() {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException());
		return UserInfoResponseDto.fromEntity(user);
	}
}
