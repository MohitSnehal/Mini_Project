package com.ads.mini_project.pojo.response;

import com.ads.mini_project.data.BloomFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomFilterResponse {
    private double falsePositivePercentage;
}
