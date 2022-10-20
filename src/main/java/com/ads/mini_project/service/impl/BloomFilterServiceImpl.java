package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.BloomFilter;
import com.ads.mini_project.pojo.request.BloomFilterRequest;
import com.ads.mini_project.pojo.response.BloomFilterResponse;
import com.ads.mini_project.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterServiceImpl implements BloomFilterService {
    @Override
    public BloomFilterResponse process(BloomFilterRequest request) {
        BloomFilter bloomFilter = new BloomFilter(16, 6);
        bloomFilter.add(request.getStr());
        return BloomFilterResponse.builder()
                .build();
    }
}
