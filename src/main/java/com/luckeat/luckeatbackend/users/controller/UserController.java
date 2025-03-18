package com.luckeat.luckeatbackend.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.common.exception.user.EmailDuplicateException;
import com.luckeat.luckeatbackend.common.exception.user.NicknameDuplicateException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.users.dto.LoginRequestDto;
import com.luckeat.luckeatbackend.users.dto.LoginResponseDto;
import com.luckeat.luckeatbackend.users.dto.NicknameUpdateDto;
import com.luckeat.luckeatbackend.users.dto.PasswordUpdateDto;
import com.luckeat.luckeatbackend.users.dto.RegisterRequestDto;
import com.luckeat.luckeatbackend.users.dto.UserInfoResponseDto;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 회원가입, 로그인, 사용자 정보 조회/수정/삭제 등의 기능 제공
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;


	/**
	 * 특정 ID의 사용자를 조회합니다.
	 * 
	 * @param userId 조회할 사용자의 ID
	 * @return 조회된 사용자 정보
	 * @throws UserNotFoundException 사용자가 존재하지 않는 경우 발생
	 */
	@GetMapping("/{user_id}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getUserById(userId)
				.orElseThrow(() -> new UserNotFoundException()));
	}

	/**
	 * 새로운 사용자를 등록합니다 (회원가입).
	 * 
	 * @param registerRequestDto 회원가입 정보 (이메일, 비밀번호, 닉네임 등)
	 * @return 생성된, 민감 정보가 제외된 사용자 정보와 201 Created 상태 코드
	 * @throws EmailDuplicateException 이메일이 이미 사용 중인 경우 발생
	 * @throws NicknameDuplicateException 닉네임이 이미 사용 중인 경우 발생
	 */
	@PostMapping("/register")
	public ResponseEntity<User> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
		// 중복 검사는 서비스 계층에서 처리
		User user = userService.createUser(registerRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	/**
	 * 사용자 로그인을 처리합니다.
	 * 
	 * @param loginRequestDto 로그인 정보 (이메일, 비밀번호)
	 * @return 로그인 결과 (JWT 토큰 포함)
	 * @throws com.luckeat.luckeatbackend.common.exception.user.InvalidCredentialsException 인증 정보가 잘못된 경우 발생
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
		// 예외는 GlobalExceptionHandler에서 처리됨
		LoginResponseDto response = userService.login(loginRequestDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * 닉네임 중복 여부를 확인합니다.
	 * 
	 * @param nickname 중복 확인할 닉네임
	 * @return 중복이 없는 경우 200 OK
	 * @throws NicknameDuplicateException 닉네임이 이미 사용 중인 경우 발생
	 */
	@GetMapping("/nicknameValid")
	public ResponseEntity<Void> checkNicknameValidity(@RequestParam String nickname) {
		boolean nicknameExists = userService.existsByNickname(nickname);
		if (nicknameExists) {
			throw new NicknameDuplicateException();
		}
		return ResponseEntity.ok().build();
	}

	/**
	 * 사용자 로그아웃을 처리합니다.
	 * 
	 * @return 로그아웃 성공 메시지
	 */
	@PostMapping("/logout")
	public ResponseEntity<String> logoutUser() {
		return ResponseEntity.ok("User logged out successfully.");
	}

	/**
	 * 이메일 중복 여부를 확인합니다.
	 * 
	 * @param email 중복 확인할 이메일
	 * @return 중복이 없는 경우 200 OK
	 * @throws EmailDuplicateException 이메일이 이미 사용 중인 경우 발생
	 */
	@GetMapping("/emailValid")
	public ResponseEntity<Void> checkEmailValidity(@RequestParam String email) {
		boolean emailExists = userService.existsByEmail(email);
		if (emailExists) {
			throw new EmailDuplicateException();
		}
		return ResponseEntity.ok().build();
	}

	/**
	 * 현재 로그인한 사용자의 정보를 조회합니다.
	 * 
	 * @return 현재 사용자 정보
	 * @throws com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@GetMapping("/me")
	public ResponseEntity<UserInfoResponseDto> getCurrentUser() {
		// 예외는 GlobalExceptionHandler에서 처리됨
		UserInfoResponseDto response = userService.getCurrentUserInfo();
		return ResponseEntity.ok(response);
	}

	/**
	 * 현재 로그인한 사용자의 닉네임을 수정합니다.
	 * 
	 * @param nicknameUpdateDto 새로운 닉네임 정보
	 * @return 성공 시 200 OK
	 * @throws NicknameDuplicateException 닉네임이 이미 사용 중인 경우 발생
	 * @throws com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@PatchMapping("/nickname")
	public ResponseEntity<Void> updateNickname(@Valid @RequestBody NicknameUpdateDto nicknameUpdateDto) {
		// 예외는 GlobalExceptionHandler에서 처리됨
		userService.updateNickname(nicknameUpdateDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * 현재 로그인한 사용자의 비밀번호를 수정합니다.
	 * 
	 * @param passwordUpdateDto 새로운 비밀번호 정보 (현재 비밀번호, 새 비밀번호)
	 * @return 성공 시 200 OK
	 * @throws com.luckeat.luckeatbackend.common.exception.user.InvalidCredentialsException 현재 비밀번호가 일치하지 않는 경우 발생
	 * @throws com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateDto passwordUpdateDto) {
		// 예외는 GlobalExceptionHandler에서 처리됨
		userService.updatePassword(passwordUpdateDto);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 현재 로그인한 사용자를 탈퇴 처리합니다 (논리적 삭제).
	 * 
	 * @return 성공 시 204 No Content
	 * @throws com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@DeleteMapping
	public ResponseEntity<Void> deleteCurrentUser() {
		// 예외는 GlobalExceptionHandler에서 처리됨
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
}
