package com.ads.mini_project.service;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.CountMinSketchResponse;

public interface CountMinSketchService {
    CountMinSketchResponse process(TestCaseTypeEnum testCaseTypeEnum) throws Exception;
}
