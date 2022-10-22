package com.ads.mini_project.enums;

import lombok.Getter;

@Getter
public enum TestCaseTypeEnum {
    NON_REPEATING(0, "test_case_0.in"),
    REPEATING(1, "test_case_1.in"),
    TW0_MILLION_AND_REPEATING(2, "test_case_2.in"),
    FOUR_MILLION_AND_REPEATING(3, "test_case_3.in"),
    TEN_MILLION_AND_REPEATING(4, "test_case_4.in"),
    SMALLEST_DATA_SET(5, "test_case_5.in");
    

    int code;
    String fileName;

    TestCaseTypeEnum(int code, String fileName) {
        this.code = code;
        this.fileName = fileName;
    }

    public static TestCaseTypeEnum valueOf(Integer code) {
        for(TestCaseTypeEnum testCaseType: TestCaseTypeEnum.values()){
            if(testCaseType.getCode() == code) {
                return testCaseType;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return this.name();
    }
}
