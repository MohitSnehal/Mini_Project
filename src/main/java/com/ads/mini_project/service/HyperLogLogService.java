package com.ads.mini_project.service;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import com.ads.mini_project.pojo.response.HyperLogLogResponse;

public interface HyperLogLogService {
    HyperLogLogResponse processV1(TestCaseTypeEnum testCaseTypeEnum) throws Exception;
    HyperLogLogResponse processV2(TestCaseTypeEnum testCaseTypeEnum) throws Exception;
}
