package com.luckeat.luckeatbackend.users.controller;

import java.util.List;

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

import com.luckeat.luckeatbackend.users.dto.LoginRequestDto;
import com.luckeat.luckeatbackend.users.dto.LoginResponseDto;
import com.luckeat.luckeatbackend.users.dto.NicknameUpdateDto;
import com.luckeat.luckeatbackend.users.dto.PasswordUpdateDto;
import com.luckeat.luckeatbackend.users.dto.UserInfoResponseDto;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// 모든 유저 조회
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	// 특정 유저 조회
	@GetMapping("/{user_id}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		return userService.getUserById(userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/register")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		if (userService.existsByEmail(user.getEmail())) {
			return ResponseEntity.badRequest().body("Email already in use");
		}

		if (userService.existsByNickname(user.getNickname())) {
			return ResponseEntity.badRequest().body("nickname already in use");
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
		try {
			LoginResponseDto response = userService.login(loginRequestDto);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	// 닉네임 중복 확인
	@GetMapping("/nicknameValid")
	public ResponseEntity<?> checkNicknameValidity(@RequestParam String nickname) {
		// 닉네임이 이미 존재하는지 확인
		boolean nicknameExists = userService.existsByNickname(nickname);

		if (nicknameExists) {
			// 닉네임이 중복되면 200 OK 응답
			return ResponseEntity.ok().build();
		} else {
			// 닉네임이 존재하지 않으면 401 Unauthorized 응답
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<String> logoutUser() {
		return ResponseEntity.ok("User logged out successfully.");
	}

	@GetMapping("/emailValid")
	public ResponseEntity<?> checkEmailValidity(@RequestParam String email) {
		// 이메일이 이미 존재하는지 확인
		boolean emailExists = userService.existsByEmail(email);

		if (emailExists) {
			// 이메일이 중복되면 200 OK 응답
			return ResponseEntity.ok().build();
		} else {
			// 이메일이 존재하지 않으면 401 Unauthorized 응답
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/me")
	public ResponseEntity<UserInfoResponseDto> getCurrentUser() {
		try {
			UserInfoResponseDto response = userService.getCurrentUserInfo();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PatchMapping("/nickname")
	public ResponseEntity<?> updateNickname(@RequestBody NicknameUpdateDto nicknameDto) {
		try {
			// 서비스에서 JWT 토큰으로부터 사용자 ID를 가져옴
			userService.updateNickname(nicknameDto);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 비밀번호 수정
	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto passwordDto) {
		try {
			// 서비스에서 JWT 토큰으로부터 사용자 ID를 가져옴
			userService.updatePassword(passwordDto);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	// 회원 삭제
	@DeleteMapping
	public ResponseEntity<Void> deleteCurrentUser() {
		try {
			userService.deleteCurrentUser();
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
