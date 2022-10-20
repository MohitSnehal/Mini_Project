package com.ads.mini_project.service.impl;

import com.ads.mini_project.data.CountMinSketch;
import com.ads.mini_project.pojo.response.CountMinSketchResponse;
import com.ads.mini_project.service.CountMinSketchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountMinSketchServiceImpl implements CountMinSketchService {

    @Override
    public CountMinSketchResponse process(int totalTestCases) {
        log.info("started CountMinSketch totalTestCases: [{}]", totalTestCases);
        int[] actualFreq = new int[100];
        Random rand = new Random(321);
        CountMinSketch cms = new CountMinSketch();
        for (int i = 0; i < totalTestCases; i++) {
            int idx = rand.nextInt(actualFreq.length);
            cms.setInt(idx);
            actualFreq[idx] += 1;
        }
        return CountMinSketchResponse.builder()
                .response("SUCCESS")
                .build();
    }
}
