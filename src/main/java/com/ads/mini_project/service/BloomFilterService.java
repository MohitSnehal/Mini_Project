package com.ads.mini_project.service;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.BloomFilterResponse;

public interface BloomFilterService {
    BloomFilterResponse process(TestCaseTypeEnum totalTestCases) throws Exception;
}
