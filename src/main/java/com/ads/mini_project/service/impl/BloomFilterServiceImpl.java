package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.BloomFilter;
import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.request.BloomFilterRequest;
import com.ads.mini_project.pojo.response.BloomFilterResponse;
import com.ads.mini_project.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterServiceImpl implements BloomFilterService {
    @Override
    public BloomFilterResponse process(TestCaseTypeEnum testType) throws Exception {
        //keeping the most optimised bloom filter configuration as default
        HashSet<Integer> actualSet = null;
        HashMap<Integer, List<Pair<Integer,Integer>>> falsePositivesMap = new HashMap<>();
        int totalTestCases = 0;
        for(int log2NoOfBits = 18; log2NoOfBits <= 23; log2NoOfBits++) {
            for(int noOfHashes = 3; noOfHashes <= 8; noOfHashes++) {
                totalTestCases = processHelper(testType, actualSet, falsePositivesMap, log2NoOfBits, noOfHashes);
            }
        }
        
        return BloomFilterResponse.builder()
                .streamSize(totalTestCases)
                .testCaseType(testType)
                .falsePositivesCount(falsePositivesMap)
                .build();
    }

    private int processHelper(TestCaseTypeEnum testType, HashSet<Integer> actualSet, HashMap<Integer, List<Pair<Integer,Integer>>> falsePositivesMap, int log2NoOfBits, int noOfHashes) throws Exception {
        BloomFilter bloomFilter = new BloomFilter(log2NoOfBits, noOfHashes);
        
        int totalTestCases= 0;

        Class clazz = BloomFilterServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream("/" + testType.getFileName());
        
        if(actualSet == null) {
            actualSet = new HashSet<>();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                totalTestCases = Integer.parseInt(br.readLine());
                String line;
                while ((line = br.readLine()) != null) {
                    int idx = Integer.parseInt(line);
                    bloomFilter.add(String.valueOf(idx));
                    if(!actualSet.contains(idx)) {
                        actualSet.add(idx);
                    }
                }
            }
        } else {
            for(int idx: actualSet){
                bloomFilter.add(String.valueOf(idx));
            }
        }
        
        int falsePositives = 0;
        for(int i = 0 ; i <= actualSet.size(); i++) {
//            log.info("actual set: [{}], bloomFilter: [{}]", actualSet.contains(i), bloomFilter.contains(String.valueOf(i)));
            if(!actualSet.contains(i) && bloomFilter.contains(String.valueOf(i))) {
                falsePositives++;
            }
        }
        
        if(!falsePositivesMap.containsKey(log2NoOfBits)) {
            List<Pair<Integer,Integer>> list = new LinkedList<>();
            falsePositivesMap.put(log2NoOfBits, list);
        }
        falsePositivesMap.get(log2NoOfBits).add(new ImmutablePair<>(noOfHashes, falsePositives));
        return totalTestCases;
    }
}
