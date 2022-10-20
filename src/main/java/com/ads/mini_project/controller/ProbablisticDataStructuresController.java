package com.ads.mini_project.controller;

import com.ads.mini_project.pojo.request.BloomFilterRequest;
import com.ads.mini_project.service.BloomFilterService;
import com.ads.mini_project.service.CountMinSketchService;
import com.ads.mini_project.util.Constant;
import com.ads.mini_project.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@Slf4j
@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class ProbablisticDataStructuresController {
    private final BloomFilterService bloomFilterService;
    private final CountMinSketchService countMinSketchService;

    @PostMapping(value = "/bloom_filter")
    public ResponseEntity add(@RequestBody BloomFilterRequest request) throws Exception {
        return Util.prepareSuccessResponse("BloomFilter", Constant.SUCCESS, bloomFilterService.process(request));
    }

    @GetMapping(value = "/count_min")
    public ResponseEntity add(@PathParam("totalTestCases") Integer totalTestCases) throws Exception {
        log.info("started Count Min API: [{}]", totalTestCases);
        return Util.prepareSuccessResponse("CountMinSketch", Constant.SUCCESS, countMinSketchService.process(totalTestCases));
    }

}
