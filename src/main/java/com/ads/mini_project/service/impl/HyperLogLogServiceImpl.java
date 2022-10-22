package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.HyperLogLog;
import com.ads.mini_project.data.HyperLogLogV2;
import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.HyperLogLogResponse;
import com.ads.mini_project.service.HyperLogLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class HyperLogLogServiceImpl implements HyperLogLogService {

    @Override
    public HyperLogLogResponse processV1(TestCaseTypeEnum testCaseTypeEnum) throws Exception {
        HyperLogLog hyperLogLog64Bit = new HyperLogLog(64);
        HyperLogLog hyperLogLog32bit = new HyperLogLog(32);
        HashSet<Integer> actualSet = new HashSet<>();

        Class clazz = BloomFilterServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream("/" + testCaseTypeEnum.getFileName());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int totalTestCases = Integer.parseInt(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                int idx = Integer.parseInt(line);
                hyperLogLog64Bit.add(String.valueOf(idx));
                hyperLogLog32bit.add(String.valueOf(idx));
                if (!actualSet.contains(idx)) {
                    actualSet.add(idx);
                }
            }
        }

        return HyperLogLogResponse.builder()
                .ndv(actualSet.size())
                .ndvFromHLL64(hyperLogLog64Bit.count())
                .ndvFromHLL32(hyperLogLog32bit.count())
                .build();
    }

    @Override
    //hyperloglog as developed by Hadoop
    public HyperLogLogResponse processV2(TestCaseTypeEnum testCaseTypeEnum) throws Exception {
        HyperLogLogV2 hyperLogLogV2 = new HyperLogLogV2(new HyperLogLogV2.HyperLogLogBuilder());
        HashSet<Integer> actualSet = new HashSet<>();

        Class clazz = BloomFilterServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream("/" + testCaseTypeEnum.getFileName());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int totalTestCases = Integer.parseInt(br.readLine());
            String line;
            while ((line = br.readLine()) != null) {
                int idx = Integer.parseInt(line);
                hyperLogLogV2.addString(String.valueOf(idx));
                if(!actualSet.contains(idx)) {
                    actualSet.add(idx);
                }
            }
        }

        return HyperLogLogResponse.builder()
                .ndv(actualSet.size())
                .ndvFromHLL64(hyperLogLogV2.estimateNumDistinctValues())
                .build();
    }
}
