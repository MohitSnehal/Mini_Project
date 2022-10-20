package com.ads.mini_project.data;

import com.ads.mini_project.util.Murmur3Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BloomFilter {

    private static final float DEFAULT_DELTA = 0.01f;
    private static final float DEFAULT_EPSILON = 0.01f;
    private final int w;
    private final int d;
    private final boolean[][] multiset;

    public BloomFilter() {
        this(DEFAULT_DELTA, DEFAULT_EPSILON);
    }

    public BloomFilter(float delta, float epsilon) {
        this.w = (int) Math.ceil(Math.exp(1.0) / epsilon);
        this.d = (int) Math.ceil(Math.log(1.0 / delta));
        this.multiset = new boolean[d][w];
    }

    public BloomFilter(int width, int depth) {
        this.w = width;
        this.d = depth;
        this.multiset = new boolean[d][w];
    }

    private BloomFilter(int width, int depth, boolean[][] ms) {
        this.w = width;
        this.d = depth;
        this.multiset = ms;
    }

    public void addString(String key) {
        add(key.getBytes());
    }

    public void add(byte[] key) {
        long hash64 = Murmur3Util.hash64(key);
        log.info("hash value: [{}]", hash64);
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);
            // hashcode should be positive, flip all the bits if it's negative
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            int pos = combinedHash % w;
            multiset[i - 1][pos] = true;
        }
    }

    public boolean contains(byte[] key) {
        long hash64 = Murmur3Util.hash64(key);
        log.info("hash value: [{}]", hash64);
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);
            // hashcode should be positive, flip all the bits if it's negative
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            int pos = combinedHash % w;
            if(!multiset[i-1][pos])
                return false;
        }
        return true;
    }
}
