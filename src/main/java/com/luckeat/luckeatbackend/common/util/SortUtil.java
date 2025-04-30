package com.luckeat.luckeatbackend.common.util; // 적절한 패키지 경로로 변경하세요

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class SortUtil {

    private static final Logger logger = LoggerFactory.getLogger(SortUtil.class);

    /**
     * API 요청의 sort 파라미터를 DB 정렬에 사용할 Sort 객체로 변환합니다.
     * (네이티브 쿼리와 함께 사용되므로 DB 컬럼명을 사용합니다)
     *
     * @param sortParam API 요청의 sort 파라미터 (예: "rating,desc", "share")
     * @return Spring Data Sort 객체
     */
    public static Sort parseSortParameter(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            //logger.debug("정렬 파라미터 없음, 정렬 안 함");
            return Sort.unsorted();
        }

        logger.info("수신된 정렬 파라미터: '{}'", sortParam);

        // 파라미터에서 프로퍼티 부분만 추출 (방향 부분은 사용 안 함)
        String property = sortParam.split(",")[0].trim().toLowerCase(); 
        
        // 기본 정렬 방향을 DESC로 설정
        Sort.Direction direction = Sort.Direction.DESC;
        //logger.debug("기본 정렬 방향: DESC (높은 값 우선)");

        String dbColumn;
        boolean useNullsLast = false;

        switch (property) {
            case "distance":
                //logger.debug("정렬 기준: distance (네이티브 쿼리 처리, Sort.unsorted 반환)");
                // 거리순은 보통 오름차순(가까운 순)이 기본이므로 예외 처리
                // 만약 거리도 내림차순(먼 순)이 기본이면 이 부분도 수정 필요
                return Sort.unsorted(); // 네이티브 쿼리에서 직접 처리하므로 여기서는 unsorted
            case "rating":
                dbColumn = "avg_rating_google";
                useNullsLast = true; // 별점 정렬 시 NULL을 마지막으로
                //logger.debug("정렬 기준: rating (DB 컬럼: {}, NULLS LAST 적용, 방향: DESC)", dbColumn);
                break;
            case "share":
                dbColumn = "share_count";
                //logger.debug("정렬 기준: share (DB 컬럼: {}, 방향: DESC)", dbColumn);
                break;
            // case "review": // 리뷰 수 정렬은 아직 미구현
            //     logger.warn("리뷰 수 기준 정렬은 현재 지원되지 않습니다.");
            //     return Sort.unsorted();
            default:
                logger.warn("알 수 없는 정렬 기준 '{}'이(가) 요청되었습니다. 정렬 없이 진행합니다.", property);
                return Sort.unsorted();
        }

        Sort.Order order = Sort.Order.by(dbColumn).with(direction);
        if (useNullsLast) {
            order = order.nullsLast();
        }

        return Sort.by(order);
    }
} 