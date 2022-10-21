package com.ads.mini_project.controller;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.service.BloomFilterService;
import com.ads.mini_project.service.CountMinSketchService;
import com.ads.mini_project.service.HyperLogLogService;
import com.ads.mini_project.util.Constant;
import com.ads.mini_project.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class ProbablisticDataStructuresController {
    private final BloomFilterService bloomFilterService;
    private final CountMinSketchService countMinSketchService;

    private final HyperLogLogService hyperLogLogService;

    @GetMapping(value = "/bloom_filter/{testType}")
    public ResponseEntity bloomFilter(@PathVariable("testType") int testType) throws Exception {
        TestCaseTypeEnum testCaseTypeEnum = TestCaseTypeEnum.valueOf(testType);
        return Util.prepareSuccessResponse("BloomFilter", Constant.SUCCESS, bloomFilterService.process(testCaseTypeEnum));
    }

    @GetMapping(value = "/count_min/{testType}")
    public ResponseEntity countMin(@PathVariable("testType") int testType) throws Exception {
        log.info("started Count Min API: [{}]", testType);
        TestCaseTypeEnum testCaseTypeEnum = TestCaseTypeEnum.valueOf(testType);
        return Util.prepareSuccessResponse("CountMinSketch", Constant.SUCCESS, countMinSketchService.process(testCaseTypeEnum));
    }

    @GetMapping(value = "/v1/hyper_log_log/{testType}")
    public ResponseEntity hyperLogLogV1(@PathVariable("testType") int testType) throws Exception {
        log.info("started hyperLogLogV1 API: [{}]", testType);
        TestCaseTypeEnum testCaseTypeEnum = TestCaseTypeEnum.valueOf(testType);
        return Util.prepareSuccessResponse("HyperLogLogV1", Constant.SUCCESS, hyperLogLogService.processV1(testCaseTypeEnum));
    }

    @GetMapping(value = "/v2/hyper_log_log/{testType}")
    public ResponseEntity hyperLogLogV2(@PathVariable("testType") int testType) throws Exception {
        log.info("started hyperLogLogV2 API: [{}]", testType);
        TestCaseTypeEnum testCaseTypeEnum = TestCaseTypeEnum.valueOf(testType);
        return Util.prepareSuccessResponse("HyperLogLogV2", Constant.SUCCESS, hyperLogLogService.processV2(testCaseTypeEnum));
    }

}
