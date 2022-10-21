package com.ads.mini_project.pojo.response;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountMinSketchResponse {
    private TestCaseTypeEnum testType;
    private int streamSize;
    private HashMap<Integer, Integer> freqMismatchCount;
}
