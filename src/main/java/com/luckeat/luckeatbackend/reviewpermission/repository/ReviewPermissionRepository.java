package com.luckeat.luckeatbackend.reviewpermission.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.reviewpermission.model.ReviewPermission;

@Repository
public interface ReviewPermissionRepository extends JpaRepository<ReviewPermission, Long> {

	 // 소프트 삭제된 항목을 제외하고 사용자 ID와 스토어 ID로 권한 조회
    Optional<ReviewPermission> findByUserIdAndStoreIdAndDeletedAtIsNull(Long userId, Long storeId);
    
    // 원래 메소드는 유지 (하위 호환성 위해)
    Optional<ReviewPermission> findByUserIdAndStoreId(Long userId, Long storeId);
    
    // 사용자의 모든 권한 조회 (소프트 삭제 제외)
    List<ReviewPermission> findByUserIdAndDeletedAtIsNull(Long userId);
    
    // 스토어에 대한 모든 권한 조회 (소프트 삭제 제외)
    List<ReviewPermission> findByStoreIdAndDeletedAtIsNull(Long storeId);
    
    // 소프트 삭제된 항목을 제외하고 권한 존재 여부 확인
    boolean existsByUserIdAndStoreIdAndDeletedAtIsNull(Long userId, Long storeId);

    // 소프트 삭제된 항목을 제외하고 권한 존재 여부 확인
    boolean existsByUserIdAndStoreId(Long userId, Long storeId);

    // 소프트 삭제된 항목을 제외하고 모든 권한 조회
    List<ReviewPermission> findByDeletedAtIsNull();

    // 소프트 삭제된 항목을 제외하고 특정 ID의 권한 조회
    Optional<ReviewPermission> findByIdAndDeletedAtIsNull(Long id);

}
