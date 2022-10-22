package com.ads.mini_project.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HyperLogLogResponse {
    private long ndv;
    private long ndvFromHLL64;
    private long ndvFromHLL32;
}
