package com.luckeat.luckeatbackend.store.dto;

// import org.springframework.data.domain.Page; // 이제 필요 없을 수 있음
import java.io.Serializable; // 추가
import java.util.List;

public class StoreQueryResult implements Serializable { // Serializable 구현 추가
    // serialVersionUID 추가 (클래스 변경 시 호환성 관리를 위해 권장)
    private static final long serialVersionUID = 1L;

    private List<StoreListDto> content;
    private long totalElements;
    private int totalPages;
    private long queryExecutionTimeMs;

    public StoreQueryResult() {}

    public StoreQueryResult(List<StoreListDto> content, long totalElements, int totalPages, long queryExecutionTimeMs) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.queryExecutionTimeMs = queryExecutionTimeMs;
    }

    public List<StoreListDto> getContent() {
        return content;
    }

    public void setContent(List<StoreListDto> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getQueryExecutionTimeMs() {
        return queryExecutionTimeMs;
    }

    public void setQueryExecutionTimeMs(long queryExecutionTimeMs) {
        this.queryExecutionTimeMs = queryExecutionTimeMs;
    }
} 