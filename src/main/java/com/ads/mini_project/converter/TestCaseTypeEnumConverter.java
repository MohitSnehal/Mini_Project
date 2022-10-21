package com.ads.mini_project.converter;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class TestCaseTypeEnumConverter implements Converter<Integer, TestCaseTypeEnum> {
    @Override
    public TestCaseTypeEnum convert(Integer code) {
        log.info("found code: {}", code);
        return TestCaseTypeEnum.valueOf(code);
    }
}
