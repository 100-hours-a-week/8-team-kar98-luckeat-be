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

import com.luckeat.luckeatbackend.security.jwt.JwtTokenProvider;
import com.luckeat.luckeatbackend.users.dto.LoginRequestDto;
import com.luckeat.luckeatbackend.users.dto.LoginResponseDto;
import com.luckeat.luckeatbackend.users.dto.NicknameUpdateDto;
import com.luckeat.luckeatbackend.users.dto.PasswordUpdateDto;
import com.luckeat.luckeatbackend.users.dto.UserInfoResponseDto;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

	@Transactional
	public User createUser(User user) {
		validateCreateUserRequest(user);
		// 비밀번호 암호화
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	// 닉네임만 수정하는 메소드
	@Transactional
	public User updateNickname(NicknameUpdateDto nicknameDto) {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));
		String newNickname = nicknameDto.getNickname();

		validateNicknameUpdate(newNickname);
		// 새 닉네임이 이미 사용 중인지 확인
		if (userRepository.existsByNicknameAndDeletedAtIsNull(newNickname) && !user.getNickname().equals(newNickname)) {
			throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + newNickname);
		}

		user.setNickname(newNickname);
		return userRepository.save(user);
	}

	// 비밀번호 수정 메소드 - DTO를 받는 버전
	@Transactional
	public User updatePassword(PasswordUpdateDto passwordDto) {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

		String currentPassword = passwordDto.getCurrentPassword();
		String newPassword = passwordDto.getNewPassword();
		String confirmPassword = passwordDto.getConfirmPassword();

		validatePasswordUpdate(currentPassword, newPassword, confirmPassword, user.getPassword());

		// 새 비밀번호 암호화 후 저장
		user.setPassword(passwordEncoder.encode(newPassword));
		return userRepository.save(user);
	}

	// JWT 인증 기반 소프트 삭제 구현
	@Transactional
	public void deleteCurrentUser() {
		// 현재 인증된 사용자의 ID 가져오기
		Long userId = getCurrentUserId();

		// 사용자 조회
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

		// 소프트 삭제 - 물리적 삭제 대신 deletedAt 설정
		user.setDeletedAt(java.time.LocalDateTime.now());

		// 변경사항 저장
		userRepository.save(user);
	}

	public boolean existsByEmail(String email) {
		validateEmail(email);
		// 소프트 삭제된 사용자는 제외하고 이메일 존재 여부 확인
		return userRepository.existsByEmailAndDeletedAtIsNull(email);
	}

	public boolean existsByNickname(String nickname) {
		validateNickname(nickname);
		// 소프트 삭제된 사용자는 제외하고 닉네임 존재 여부 확인
		return userRepository.existsByNicknameAndDeletedAtIsNull(nickname);
	}

	// 사용자 로그인 처리
	@Transactional
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		validateLoginRequest(loginRequestDto);
		// 이메일로 사용자 조회
		User user = userRepository.findByEmail(loginRequestDto.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		// 비밀번호 검증
		if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("잘못된 비밀번호입니다.");
		}

		// 인증 객체 생성
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null,
				Collections.singleton(authority));

		// JWT 토큰 발급
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

		// 응답 DTO 구성
		return LoginResponseDto.builder().userId(user.getId()).email(user.getEmail()).nickname(user.getNickname())
				.role(user.getRole().name()).grantType("Bearer").accessToken(accessToken).refreshToken(refreshToken)
				.build();
	}

	// 현재 인증된 사용자 ID 가져오기
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			throw new IllegalStateException("인증된 사용자만 접근할 수 있습니다");
		}

		// 서비스가 아닌 리포지토리를 직접 호출
		String email = authentication.getName();
		return userRepository.findByEmailAndDeletedAtIsNull(email)
				.orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + email)).getId();
	}

	// 현재 인증된 사용자 정보 조회 - DTO 반환 버전
	public UserInfoResponseDto getCurrentUserInfo() {
		Long userId = getCurrentUserId();
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));// 기존 메소드 활용
		return UserInfoResponseDto.fromEntity(user);
	}

	// 회원가입 검증 메소드 추가
	private void validateCreateUserRequest(User user) {
		// 이메일 검증
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다");
		}

		// 이메일 형식 검증
		if (!user.getEmail()
				.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
			throw new IllegalArgumentException("유효한 이메일 형식이 아닙니다");
		}

		// 닉네임 검증
		if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
			throw new IllegalArgumentException("닉네임은 필수입니다");
		}

		// 닉네임 길이 검증
		if (user.getNickname().length() < 2 || user.getNickname().length() > 10) {
			throw new IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다");
		}

		// 비밀번호 검증
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다");
		}

		// 비밀번호 길이 검증
		if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
			throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하여야 합니다");
		}

		// 비밀번호 복잡성 검증 (숫자, 소문자를 각각 하나 이상 포함)
		if (!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,20}$")) {
			throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하이며, 영어 소문자와 숫자를 각각 최소 1개 이상 포함해야 합니다");
		}

		// 역할 검증
		if (user.getRole() == null) {
			throw new IllegalArgumentException("역할은 필수입니다");
		}

		// 역할 유효값 검증
		String roleName = user.getRole().name().toLowerCase();
		if (!roleName.equals("admin") && !roleName.equals("buyer") && !roleName.equals("seller")) {
			throw new IllegalArgumentException("역할은 ADMIN, BUYER, SELLER 중 하나여야 합니다");
		}
	}

	// 로그인 검증 메소드 추가
	private void validateLoginRequest(LoginRequestDto loginRequestDto) {
		// 이메일 검증
		if (loginRequestDto.getEmail() == null || loginRequestDto.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다");
		}

		// 비밀번호 검증
		if (loginRequestDto.getPassword() == null || loginRequestDto.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다");
		}
	}

	// 이메일 검증 메소드 추가
	private void validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다");
		}
	}

	// 닉네임 검증 메소드 추가
	private void validateNickname(String nickname) {
		if (nickname == null || nickname.trim().isEmpty()) {
			throw new IllegalArgumentException("닉네임은 필수입니다");
		}
	}

	// 닉네임 업데이트 검증 메소드 추가
	private void validateNicknameUpdate(String nickname) {
		if (nickname == null || nickname.trim().isEmpty()) {
			throw new IllegalArgumentException("닉네임은 필수입니다");
		}

		if (nickname.length() < 2 || nickname.length() > 10) {
			throw new IllegalArgumentException("닉네임은 2자 이상 10자 이하여야 합니다");
		}
	}
	// 비밀번호 업데이트 유효성 검증 메소드
	private void validatePasswordUpdate(String currentPassword, String newPassword, String confirmPassword,
			String storedPassword) {
		// 현재 비밀번호 검증
		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("현재 비밀번호는 필수입니다");
		}

		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(currentPassword, storedPassword)) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다");
		}

		// 새 비밀번호 검증
		if (newPassword == null || newPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("새 비밀번호는 필수입니다");
		}

		// 비밀번호 길이 검증
		if (newPassword.length() < 8 || newPassword.length() > 20) {
			throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하여야 합니다");
		}

		// 새 비밀번호와 확인 비밀번호가 일치하는지 확인
		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다");
		}

		// 현재 비밀번호와 새 비밀번호가 같은지 확인
		if (passwordEncoder.matches(newPassword, storedPassword)) {
			throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다");
		}

		// 비밀번호 복잡성 검증 (숫자, 특수문자, 대소문자 포함)
		if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,20}$")) {
			throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하이며, 영어 소문자와 숫자를 각각 최소 1개 이상 포함해야 합니다");
		}
	}
}
