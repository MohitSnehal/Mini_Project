package com.ads.mini_project.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BloomFilterRequest {
    private int logNoOfBits;
    private int noOfHashes;
}
