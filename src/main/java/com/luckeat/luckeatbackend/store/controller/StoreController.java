package com.luckeat.luckeatbackend.store.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.luckeat.luckeatbackend.store.dto.StoreListDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreQueryResult;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "가게 API", description = "가게 정보 관련 API 목록")
public class StoreController {

	private static final Logger logger = LoggerFactory.getLogger(StoreController.class);
	private final StoreService storeService;

	/**
	 * 가게 목록을 조회합니다.
	 * 
	 * @param lat 현재 위치 위도
	 * @param lng 현재 위치 경도
	 * @param radius 검색 반경 (km)
	 * @param sort 정렬 기준 (distance, rating, discount)
	 * @param storeName 가게 이름 검색어
	 * @param isDiscountOpen 할인 중인 가게만 조회 여부
	 * @param page 페이지 번호 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 가게 목록 정보
	 */
	@Operation(summary = "가게 목록 조회", description = "다양한 조건으로 가게 목록을 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "가게 목록 조회 성공")
	})
	@GetMapping
	public ResponseEntity<Page<StoreListDto>> getAllStores(
			@Parameter(description = "현재 위치 위도") @RequestParam(required = false) Double lat, 
			@Parameter(description = "현재 위치 경도") @RequestParam(required = false) Double lng,
			@Parameter(description = "검색 반경 (km)") @RequestParam(required = false) Double radius, 
			@Parameter(description = "정렬 기준 (distance, rating, share)") @RequestParam(required = false) String sort,
			@Parameter(description = "가게 이름 검색어") @RequestParam(required = false) String storeName, 
			@Parameter(description = "할인 중인 가게만 조회 여부") @RequestParam(required = false) Boolean isDiscountOpen,
			@Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
			@Parameter(description = "카테고리") @RequestParam(defaultValue = "0") int categoryId) {

		StoreQueryResult queryResult = storeService.getStores(lat, lng, radius, sort, storeName, isDiscountOpen, page, size, categoryId, false);
		Pageable pageable = PageRequest.of(page, size, parseSortParameter(sort));
		Page<StoreListDto> pageResult = new PageImpl<>(queryResult.getContent(), pageable, queryResult.getTotalElements());
		return ResponseEntity.ok(pageResult);
	}

	@Operation(summary = "가게 목록 조회 성능 테스트 (DB vs Cache)", description = "가게 목록 조회 API를 DB 직접 조회와 캐시 조회 각각 100번 호출하여 성능 지표(p99)를 측정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성능 테스트 결과 반환")
	})
	@GetMapping("/test/performance-test")
	public ResponseEntity<Map<String, Object>> runStoreSearchPerformanceTest(
			@Parameter(description = "현재 위치 위도") @RequestParam(required = false) Double lat,
			@Parameter(description = "현재 위치 경도") @RequestParam(required = false) Double lng,
			@Parameter(description = "검색 반경 (km)") @RequestParam(required = false) Double radius,
			@Parameter(description = "정렬 기준 (distance, rating, share)") @RequestParam(required = false) String sort,
			@Parameter(description = "가게 이름 검색어") @RequestParam(required = false) String storeName,
			@Parameter(description = "할인 중인 가게만 조회 여부") @RequestParam(required = false) Boolean isDiscountOpen,
			@Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
			@Parameter(description = "카테고리") @RequestParam(defaultValue = "0") int categoryId) {

		int iterations = 1000;
		Map<String, Object> results = new HashMap<>();
		Pageable pageable = PageRequest.of(page, size, parseSortParameter(sort));
		Page<StoreListDto> lastResultPage = null;

		// --- DB 직접 조회 테스트 ---
		List<Long> dbExecutionTimes = new ArrayList<>(iterations);
		int successfulDbIterations = 0;
		logger.info("DB 직접 조회 성능 테스트 시작 ({}회 반복)", iterations);

		for (int i = 0; i < iterations; i++) {
			try {
				long apiStartTime = System.nanoTime();
				StoreQueryResult queryResult = storeService.getStores(lat, lng, radius, sort, storeName, isDiscountOpen, page, size, categoryId, true);
				lastResultPage = new PageImpl<>(queryResult.getContent(), pageable, queryResult.getTotalElements());
				long apiEndTime = System.nanoTime();
				dbExecutionTimes.add(apiEndTime - apiStartTime);
				successfulDbIterations++;
			} catch (Exception e) {
				logger.error("DB 테스트 반복 중 오류 발생 (반복 {}): {}", i + 1, e.getMessage(), e);
			}
		}

		if (successfulDbIterations > 0) {
			Collections.sort(dbExecutionTimes);
			// p90, p95, p99 모두 계산
			long p90DbTimeNano = dbExecutionTimes.get((int) Math.ceil(0.90 * successfulDbIterations) - 1);
			long p95DbTimeNano = dbExecutionTimes.get((int) Math.ceil(0.95 * successfulDbIterations) - 1);
			long p99DbTimeNano = dbExecutionTimes.get((int) Math.ceil(0.99 * successfulDbIterations) - 1);
			
			double p90DbTimeMs = p90DbTimeNano / 1_000_000.0;
			double p95DbTimeMs = p95DbTimeNano / 1_000_000.0;
			double p99DbTimeMs = p99DbTimeNano / 1_000_000.0;
			
			results.put("db_p90ExecutionTimeMs", p90DbTimeMs);
			results.put("db_p95ExecutionTimeMs", p95DbTimeMs);
			results.put("db_p99ExecutionTimeMs", p99DbTimeMs);
			
			logger.info("DB 직접 조회 성능 테스트 완료: P90 = {}ms, P95 = {}ms, P99 = {}ms (성공: {}/{})", 
				String.format("%.3f", p90DbTimeMs),
				String.format("%.3f", p95DbTimeMs),
				String.format("%.3f", p99DbTimeMs), 
				successfulDbIterations, iterations);
		} else {
			results.put("db_p90ExecutionTimeMs", "N/A");
			results.put("db_p95ExecutionTimeMs", "N/A");
			results.put("db_p99ExecutionTimeMs", "N/A");
			logger.warn("DB 테스트 성공적인 반복이 없어 백분위수를 계산할 수 없습니다.");
		}

		// --- 캐시 조회 테스트 ---
		List<Long> cacheExecutionTimes = new ArrayList<>(iterations);
		int successfulCacheIterations = 0;
		logger.info("캐시 조회 성능 테스트 시작 ({}회 반복)", iterations);

		// 캐시 예열 (Warm-up)
		try {
		 storeService.getStores(lat, lng, radius, sort, storeName, isDiscountOpen, page, size, categoryId, false);
		 logger.info("캐시 예열 완료.");
		} catch (Exception e) {
			logger.warn("캐시 예열 중 오류 발생: {}", e.getMessage());
		}

		for (int i = 0; i < iterations; i++) {
			try {
				long apiStartTime = System.nanoTime();
				StoreQueryResult queryResult = storeService.getStores(lat, lng, radius, sort, storeName, isDiscountOpen, page, size, categoryId, false);
				if (i == iterations -1) {
					lastResultPage = new PageImpl<>(queryResult.getContent(), pageable, queryResult.getTotalElements());
				}
				long apiEndTime = System.nanoTime();
				cacheExecutionTimes.add(apiEndTime - apiStartTime);
				successfulCacheIterations++;
			} catch (Exception e) {
				logger.error("캐시 테스트 반복 중 오류 발생 (반복 {}): {}", i + 1, e.getMessage(), e);
			}
		}

		if (successfulCacheIterations > 0) {
			Collections.sort(cacheExecutionTimes);
			// p90, p95, p99 모두 계산
			long p90CacheTimeNano = cacheExecutionTimes.get((int) Math.ceil(0.90 * successfulCacheIterations) - 1);
			long p95CacheTimeNano = cacheExecutionTimes.get((int) Math.ceil(0.95 * successfulCacheIterations) - 1);
			long p99CacheTimeNano = cacheExecutionTimes.get((int) Math.ceil(0.99 * successfulCacheIterations) - 1);
			
			double p90CacheTimeMs = p90CacheTimeNano / 1_000_000.0;
			double p95CacheTimeMs = p95CacheTimeNano / 1_000_000.0;
			double p99CacheTimeMs = p99CacheTimeNano / 1_000_000.0;
			
			results.put("cache_p90ExecutionTimeMs", p90CacheTimeMs);
			results.put("cache_p95ExecutionTimeMs", p95CacheTimeMs);
			results.put("cache_p99ExecutionTimeMs", p99CacheTimeMs);
			
			logger.info("캐시 조회 성능 테스트 완료: P90 = {}ms, P95 = {}ms, P99 = {}ms (성공: {}/{})", 
				String.format("%.3f", p90CacheTimeMs),
				String.format("%.3f", p95CacheTimeMs),
				String.format("%.3f", p99CacheTimeMs), 
				successfulCacheIterations, iterations);
		} else {
			results.put("cache_p90ExecutionTimeMs", "N/A");
			results.put("cache_p95ExecutionTimeMs", "N/A");
			results.put("cache_p99ExecutionTimeMs", "N/A");
			logger.warn("캐시 테스트 성공적인 반복이 없어 백분위수를 계산할 수 없습니다.");
		}

		results.put("totalIterationsPerTest", iterations);
		if (lastResultPage != null) {
			results.put("lastResultTotalElements", lastResultPage.getTotalElements());
			results.put("lastResultTotalPages", lastResultPage.getTotalPages());
		} else {
			// 두 테스트 모두 실패했거나 결과를 얻지 못한 경우
			results.put("lastResult", "N/A (테스트 실패 또는 결과 없음)");
		}

		return ResponseEntity.ok(results);
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

	private Sort parseSortParameter(String sortParam) {
		if (sortParam == null || sortParam.isBlank()) {
			return Sort.unsorted();
		}
		String[] parts = sortParam.split(",");
		String property = parts[0];
		Sort.Direction direction = Sort.Direction.ASC;
		if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
			direction = Sort.Direction.DESC;
		}
		if ("distance".equals(property)) {
			return Sort.unsorted();
		} else if ("rating".equals(property)) {
			property = "avgRatingGoogle";
		} else if ("share".equals(property)) {
			property = "shareCount";
		}
		return Sort.by(direction, property);
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
	public ResponseEntity<Void> updateStore(
			@PathVariable("store_id") Long storeId,
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

	@Operation(summary = "내 가게 정보 조회", 
			  description = "현재 로그인한 사용자의 가게 정보를 조회합니다", 
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

