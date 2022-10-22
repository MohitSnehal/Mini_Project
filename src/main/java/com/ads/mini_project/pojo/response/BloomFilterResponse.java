package com.ads.mini_project.pojo.response;

import com.ads.mini_project.enums.TestCaseTypeEnum;
import org.apache.commons.lang3.tuple.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomFilterResponse {
    private TestCaseTypeEnum testCaseType;
    private int streamSize;
    private HashMap<Integer, List<Pair<Integer,Integer>>> falsePositivesCount;
}
