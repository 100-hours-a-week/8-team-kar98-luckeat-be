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
	public ResponseEntity<?> loginUser(@RequestBody User user) {
		// TODO : 로그인 컨트롤러 기능 구현
		return null;
	}

	// 닉네임 중복 확인
	@GetMapping("/nicknameValid")
	public ResponseEntity<Boolean> checkNicknameValidity(@RequestParam String nickname) {
		return ResponseEntity.ok(!userService.existsByNickname(nickname));
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<String> logoutUser() {
		return ResponseEntity.ok("User logged out successfully.");
	}

	// 회원 정보 조회
	@GetMapping("/me")
	public ResponseEntity<?> getMyInfo(@RequestParam Long userId) {
		return userService.getUserById(userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PatchMapping("/nickname")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
		return userService.getUserById(userId).map(existingUser -> {
			user.setId(userId);
			return ResponseEntity.ok(userService.updateUser(user));
		}).orElse(ResponseEntity.notFound().build());
	}

	// 비밀번호 수정
	@PatchMapping("/password")
	public ResponseEntity<?> updatePassword(@RequestParam Long userId, @RequestBody User user) {
		return userService.getUserById(userId).map(existingUser -> {
			existingUser.setPassword(user.getPassword());
			return ResponseEntity.ok(userService.updateUser(existingUser));
		}).orElse(ResponseEntity.notFound().build());
	}

	// 회원 삭제
	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		// TODO: 소프트 삭제로 바꾸기
		return userService.getUserById(userId).map(user -> {
			userService.deleteUser(userId);
			return ResponseEntity.noContent().<Void>build();
		}).orElse(ResponseEntity.notFound().build());
	}
}
