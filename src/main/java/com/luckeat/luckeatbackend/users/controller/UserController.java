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
import com.luckeat.luckeatbackend.common.exception.user.UserInvalidNicknameException;
import com.luckeat.luckeatbackend.users.dto.LoginRequestDto;
import com.luckeat.luckeatbackend.users.dto.LoginResponseDto;
import com.luckeat.luckeatbackend.users.dto.NicknameUpdateDto;
import com.luckeat.luckeatbackend.users.dto.PasswordUpdateDto;
import com.luckeat.luckeatbackend.users.dto.RegisterRequestDto;
import com.luckeat.luckeatbackend.users.dto.UserInfoResponseDto;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 회원가입, 로그인, 사용자 정보 조회/수정/삭제 등의 기능 제공
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 계정 관련 API 목록")
public class UserController {

	private final UserService userService;


	/**
	 * 새로운 사용자를 등록합니다 (회원가입).
	 * 
	 * @param registerRequestDto 회원가입 정보 (이메일, 비밀번호, 닉네임 등)
	 * @return 생성된, 민감 정보가 제외된 사용자 정보와 201 Created 상태 코드
	 * @throws EmailDuplicateException 이메일이 이미 사용 중인 경우 발생
	 * @throws NicknameDuplicateException 닉네임이 이미 사용 중인 경우 발생
	 */
	@Operation(summary = "사용자 등록", description = "새로운 사용자를 시스템에 등록합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 사용자", content = {
			@Content(schema = @Schema(oneOf = {
				EmailDuplicateException.class,
				NicknameDuplicateException.class
			}), mediaType = "application/json")
		})
	})
	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
		// 중복 검사는 서비스 계층에서 처리
		User user = userService.createUser(registerRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 사용자 로그인을 처리합니다.
	 * 
	 * @param loginRequestDto 로그인 정보 (이메일, 비밀번호)
	 * @return 로그인 결과 (JWT 토큰 포함)
	 * @throws com.luckeat.luckeatbackend.common.exception.user.InvalidCredentialsException 인증 정보가 잘못된 경우 발생
	 */
	@Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
	})
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
		// 예외는 GlobalExceptionHandler에서 처리됨
		LoginResponseDto response = userService.login(loginRequestDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * 닉네임 중복 여부와 유효성을 확인합니다.
	 * 
	 * @param nickname 검증할 닉네임
	 * @return 유효하고 중복이 없는 경우 200 OK
	 * @throws NicknameDuplicateException 닉네임이 이미 사용 중인 경우 발생
	 * @throws UserInvalidNicknameException 닉네임 길이가 유효하지 않은 경우 발생
	 */
	@GetMapping("/nicknameValid")
	@Operation(summary = "닉네임 중복 및 유효성 확인", description = "닉네임 중복 여부와 유효성(길이 제한 2~10자)을 확인합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "닉네임 유효 및 중복 없음"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "409", description = "닉네임 중복")
		})
	public ResponseEntity<Void> checkNicknameValidity(
            @RequestParam @jakarta.validation.constraints.Size(min = 2, max = 10, message = "닉네임은 2~10자 이내여야 합니다.") String nickname) {
        
        // 중복 검증
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
	@Operation(summary = "사용자 로그아웃", description = "사용자 로그아웃을 처리합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃 성공")
	})
	public ResponseEntity<Void> logoutUser() {
		return ResponseEntity.ok().build();
	}

	/**
	 * 이메일 중복 여부를 확인합니다.
	 * 
	 * @param email 중복 확인할 이메일
	 * @return 중복이 없는 경우 200 OK
	 * @throws EmailDuplicateException 이메일이 이미 사용 중인 경우 발생
	 */
	@GetMapping("/emailValid")
	@Operation(summary = "이메일 중복 여부 확인", description = "이메일 중복 여부를 확인합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "이메일 중복 없음"),
		@ApiResponse(responseCode = "400", description = "잘못된 이메일 형식"),
		@ApiResponse(responseCode = "409", description = "이메일 중복")
	})
	public ResponseEntity<Void> checkEmailValidity(
			@RequestParam @jakarta.validation.constraints.Email(message = "유효한 이메일 형식이 아닙니다") 
			@jakarta.validation.constraints.NotBlank(message = "이메일은 필수 입력 항목입니다") String email) {
		
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
	@Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
	})	
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
	@Operation(summary = "닉네임 수정", description = "현재 로그인한 사용자의 닉네임을 수정합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "닉네임 수정 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
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
	@Operation(summary = "비밀번호 수정", description = "현재 로그인한 사용자의 비밀번호를 수정합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
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
	@Operation(summary = "사용자 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "사용자 탈퇴 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	public ResponseEntity<Void> deleteCurrentUser() {
		// 예외는 GlobalExceptionHandler에서 처리됨
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
}
