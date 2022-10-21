package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.CountMinSketchV1;
import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.CountMinSketchResponse;
import com.ads.mini_project.service.CountMinSketchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountMinSketchServiceImpl implements CountMinSketchService {

    @Override
    public CountMinSketchResponse process(TestCaseTypeEnum testCaseTypeEnum) throws Exception {
        log.info("started CountMinSketch testCaseTypeEnum: [{}]", testCaseTypeEnum);
        HashMap<Integer, Integer> freqMismatchCountMap = new HashMap<>();
        HashMap<Integer, Integer> actualFreq = null;
        int streamSize = 0;
        for(int depth=5 ; depth <= 25; depth+=5) {
            streamSize = processHelper(testCaseTypeEnum, actualFreq, freqMismatchCountMap, depth);
        }
        return CountMinSketchResponse.builder()
                .testType(testCaseTypeEnum)
                .streamSize(streamSize)
                .freqMismatchCount(freqMismatchCountMap)
                .build();
    }

    private int processHelper(TestCaseTypeEnum testCaseTypeEnum, HashMap<Integer, Integer> actualFreq, HashMap<Integer, Integer> freqMismatchCountMap, Integer depth) throws Exception {
        CountMinSketchV1 cms = new CountMinSketchV1(50000, depth);
        Class clazz = CountMinSketchServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream("/" + testCaseTypeEnum.getFileName());
        
        int streamSize = 0;
        
        if(actualFreq == null) {
            actualFreq = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                streamSize = Integer.parseInt(br.readLine());
                log.info("cms width: [{}] & depth: [{}] & size in bytes: [{}]", cms.getWidth(), cms.getDepth(), cms.getSizeInBytes());
                log.info("cms total number of records (width * depth): [{}]", cms.getWidth() * cms.getDepth());
                String line;
                while ((line = br.readLine()) != null) {
                    int idx = Integer.parseInt(line);
                    cms.setString(String.valueOf(idx));
                    if(!actualFreq.containsKey(idx)) {
                        actualFreq.put(idx, 0);
                    }
                    actualFreq.put(idx, actualFreq.get(idx) + 1);
                }
            }
        } else {
            for(Map.Entry<Integer, Integer> entry :actualFreq.entrySet()) {
                int idx = entry.getKey();
                cms.setString(String.valueOf(idx));
            }
        }

        int freqMismatchCount = 0;
        HashMap<Integer, Integer> cmsFreq = new HashMap<>();
        int count = 0, cmsCount = 0;
        for(int idx: actualFreq.keySet()) {
            cmsFreq.put(idx, cms.getEstimatedCount(String.valueOf(idx).getBytes()));
            count += actualFreq.get(idx);
            cmsCount += cmsFreq.get(idx);
            double mismatchPerc = Math.abs((double)(actualFreq.get(idx) - cmsFreq.get(idx))) / (double) actualFreq.get(idx);
            if(mismatchPerc > 0.05)
                freqMismatchCount++;
            log.info("int: [{}], actual: [{}], cms: [{}], mismatchPerc: [{}]", idx, actualFreq.get(idx), cms.getEstimatedCount(String.valueOf(idx).getBytes()), mismatchPerc);
        }

        freqMismatchCountMap.put(depth, freqMismatchCount);
        log.info("found count of test cases: [{}], and total cms count: [{}]", count, cmsCount);
        return streamSize;
    }
}
