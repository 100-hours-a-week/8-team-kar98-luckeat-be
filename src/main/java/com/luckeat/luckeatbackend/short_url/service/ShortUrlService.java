package com.luckeat.luckeatbackend.short_url.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final StoreRepository storeRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public String getOriginalUrl(String hashCode) {
        Store store = storeRepository.findByStoreUrl(hashCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 URL입니다."));
        
        // 공유 카운트 증가
        store.setShareCount(store.getShareCount() + 1);
        storeRepository.save(store);

        return baseUrl + "/store/" + store.getId();
    }
} 