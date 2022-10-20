package com.ads.mini_project.service;

import com.ads.mini_project.pojo.request.BloomFilterRequest;
import com.ads.mini_project.pojo.response.BloomFilterResponse;

public interface BloomFilterService {
    BloomFilterResponse process(BloomFilterRequest request);
}
