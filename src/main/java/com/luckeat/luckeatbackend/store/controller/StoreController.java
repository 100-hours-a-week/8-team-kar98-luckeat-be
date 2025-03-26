package com.luckeat.luckeatbackend.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.common.exception.store.StoreForbiddenException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.common.exception.store.StoreUnauthenticatedException;
import com.luckeat.luckeatbackend.store.dto.MyStoreResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreDetailResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreResponseDto;
import com.luckeat.luckeatbackend.store.service.StoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "가게 API", description = "가게 정보 관련 API 목록")
public class StoreController {

	private final StoreService storeService;

	/**
	 * 가게 목록을 조회합니다.
	 * 
	 * @param categoryId 카테고리 ID를 이용한 필터링
	 * @param lat 현재 위치 위도
	 * @param lng 현재 위치 경도
	 * @param radius 검색 반경 (km)
	 * @param sort 정렬 기준 (distance, rating, discount)
	 * @param storeName 가게 이름 검색어
	 * @param isDiscountOpen 할인 중인 가게만 조회 여부
	 * @return 가게 목록 정보
	 */
	@Operation(summary = "가게 목록 조회", description = "다양한 조건으로 가게 목록을 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "가게 목록 조회 성공")
	})
	@GetMapping
	public ResponseEntity<List<StoreResponseDto>> getAllStores(
			@Parameter(description = "카테고리 ID") @RequestParam(required = false) Long categoryId,
			@Parameter(description = "현재 위치 위도") @RequestParam(required = false) Double lat, 
			@Parameter(description = "현재 위치 경도") @RequestParam(required = false) Double lng,
			@Parameter(description = "검색 반경 (km)") @RequestParam(required = false) Double radius, 
			@Parameter(description = "정렬 기준 (distance, rating, share)") @RequestParam(required = false) String sort,
			@Parameter(description = "가게 이름 검색어") @RequestParam(required = false) String storeName, 
			@Parameter(description = "할인 중인 가게만 조회 여부") @RequestParam(required = false) Boolean isDiscountOpen) {

		return ResponseEntity.ok(storeService.getStores(categoryId, lat, lng, radius, sort, storeName, isDiscountOpen));
	}

	/**
	 * 가게 상세 정보를 조회합니다.
	 * 
	 * @param storeId 조회할 가게 ID
	 * @return 가게 상세 정보
	 * @throws StoreNotFoundException 가게가 존재하지 않는 경우 발생
	 */
	@Operation(summary = "가게 상세 정보 조회", description = "가게 ID로 가게 상세 정보를 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "가게 상세 정보 조회 성공"),
		@ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
	})
	@GetMapping("/{store_id}")
	public ResponseEntity<StoreDetailResponseDto> getStoreDetailById(@PathVariable("store_id") Long storeId) {
		return ResponseEntity.ok(storeService.getStoreDetailById(storeId));
	}

	/**
	 * 새로운 가게를 등록합니다.
	 * 
	 * @param storeRequest 가게 정보
	 * @return 생성된 가게 ID와 201 Created 상태코드
	 * @throws StoreUnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "가게 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
	})
	@PostMapping
	public ResponseEntity<Void> createStore(@Valid @RequestBody StoreRequestDto storeRequest) {
		storeService.createStore(storeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 기존 가게 정보를 수정합니다.
	 * 
	 * @param storeId 수정할 가게 ID
	 * @param storeRequest 수정할 가게 정보
	 * @return 성공 시 200 OK
	 * @throws StoreNotFoundException 가게가 존재하지 않는 경우 발생
	 * @throws StoreForbiddenException 수정 권한이 없는 경우 발생
	 * @throws StoreUnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@Operation(summary = "가게 정보 수정", description = "기존 가게 정보를 수정합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "가게 정보 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
	})
	@PutMapping("/{store_id}")
	public ResponseEntity<Void> updateStore(@PathVariable("store_id") Long storeId,
			@Valid @RequestBody StoreRequestDto storeRequest) {
		storeService.updateStore(storeId, storeRequest);
		return ResponseEntity.ok().build();
	}

	/**
	 * 가게를 삭제합니다.
	 * 
	 * @param storeId 삭제할 가게 ID
	 * @return 성공 시 204 No Content
	 * @throws StoreNotFoundException 가게가 존재하지 않는 경우 발생
	 * @throws StoreForbiddenException 삭제 권한이 없는 경우 발생
	 * @throws StoreUnauthenticatedException 인증되지 않은 요청인 경우 발생
	 */
	@Operation(summary = "가게 삭제", description = "가게를 삭제합니다", security = @SecurityRequirement(name = "jwt"))
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "가게 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
	})
	@DeleteMapping("/{store_id}")
	public ResponseEntity<Void> deleteStore(@PathVariable("store_id") Long storeId) {
		storeService.deleteStore(storeId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 가게 공유 횟수를 증가시킵니다.
	 * 
	 * @param storeId 공유할 가게 ID
	 * @return 성공 시 200 OK
	 * @throws StoreNotFoundException 가게가 존재하지 않는 경우 발생
	 */
	@Operation(summary = "가게 공유 횟수 증가", description = "가게가 공유될 때마다 공유 횟수를 증가시킵니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "공유 횟수 증가 성공"),
		@ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
	})
	@PostMapping("/{store_id}/share")
	public ResponseEntity<Void> incrementShareCount(@PathVariable("store_id") Long storeId) {
		storeService.incrementShareCount(storeId);
		return ResponseEntity.ok().build();
	}

@Operation(summary = "내 가게 정보 조회", description = "현재 로그인한 사용자의 가게 정보를 조회합니다", 
          security = @SecurityRequirement(name = "jwt"))
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "내 가게 정보 조회 성공"),
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
    @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
})
@GetMapping("/my")
public ResponseEntity<MyStoreResponseDto> getMyStore() {
    return ResponseEntity.ok(storeService.getMyStore());
}
	
	
}
