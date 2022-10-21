package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.BloomFilter;
import com.ads.mini_project.data.CountMinSketchV1;
import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.BloomFilterResponse;
import com.ads.mini_project.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterServiceImpl implements BloomFilterService {
    @Override
    public BloomFilterResponse process(TestCaseTypeEnum testType) throws Exception {
        //keeping the most optimised bloom filter configuration as default
        BloomFilter bloomFilter = new BloomFilter(23, 8);
        HashSet<Integer> actualSet = new HashSet<>();

        Class clazz = BloomFilterServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream("/" + testType.getFileName());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int totalTestCases = Integer.parseInt(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                int idx = Integer.parseInt(line);
                bloomFilter.add(String.valueOf(idx));
                if(!actualSet.contains(idx)) {
                    actualSet.add(idx);
                }
            }
        }

        int falsePositives = 0;
        for(int i = 0 ; i <= 1000000; i++) {
            if(!actualSet.contains(i) && bloomFilter.contains(String.valueOf(i))) {
                falsePositives++;
            }
        }
        return BloomFilterResponse.builder()
                .falsePositivePercentage(falsePositives/actualSet.size() * 100.00)
                .build();
    }
}
