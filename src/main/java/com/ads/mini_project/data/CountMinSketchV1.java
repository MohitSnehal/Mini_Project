package com.ads.mini_project.data;

import com.ads.mini_project.util.Murmur3Util;
import lombok.extern.slf4j.Slf4j;

/*
 * Count Min sketch is a probabilistic data structure for finding the frequency of events in a
 * stream of data. The data structure accepts two parameters epsilon and delta, epsilon specifies
 * the error in estimation and delta specifies the probability that the estimation is wrong (or the
 * confidence interval). The default values are 1% estimation error (epsilon) and 99% confidence (1 - delta).
 * Reference of code: https://github.com/prasanthj/count-min-sketch/blob/master/src/main/java/com/github/prasanthj/cmsketch/CountMinSketch.java
*/

@Slf4j
public class CountMinSketchV1 {
    // 1% estimation error with 1% probability (99% confidence) that the estimation breaks this limit
    private static final float DEFAULT_DELTA = 0.01f;
    private static final float DEFAULT_EPSILON = 0.01f;
    private final int w;
    private final int d;
    private final int[][] multiset;

    public CountMinSketchV1() {
        this(DEFAULT_DELTA, DEFAULT_EPSILON);
    }

    public CountMinSketchV1(float delta, float epsilon) {
        this.w = (int) Math.ceil(Math.exp(1.0) / epsilon);
        this.d = (int) Math.ceil(Math.log(1.0 / delta));
        this.multiset = new int[d][w];
    }

    public CountMinSketchV1(int width, int depth) {
        this.w = width;
        this.d = depth;
        this.multiset = new int[d][w];
    }

    private CountMinSketchV1(int width, int depth, int[][] ms) {
        this.w = width;
        this.d = depth;
        this.multiset = ms;
    }

    public int getWidth() {
        return w;
    }

    public int getDepth() {
        return d;
    }

    public long getSizeInBytes() {
        return ((w * d) + 2) * (Integer.SIZE / 8);
    }

    public void set(byte[] key) {
        long hash64 = Murmur3Util.hash64(key);
//        log.info("hash value: [{}]", hash64);
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);
            // hashcode should be positive, flip all the bits if it's negative
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            int pos = combinedHash % w;
            multiset[i - 1][pos] += 1;
        }
//        log.info("multiset: [{}]", multiset);
    }

    public void setString(String val) {
        set(val.getBytes());
    }

    public void setInt(int val) {
        // puts int in little endian order
//        log.info("intVal: [{}]", val);
        set(intToByteArrayLE(val));
    }


    public void setLong(long val) {
        // puts long in little endian order
        set(longToByteArrayLE(val));
    }

    private static byte[] intToByteArrayLE(int val) {
        return new byte[]{(byte) (val >> 0),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24)};
    }

    private static byte[] longToByteArrayLE(long val) {
        return new byte[]{(byte) (val >> 0),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24),
                (byte) (val >> 32),
                (byte) (val >> 40),
                (byte) (val >> 48),
                (byte) (val >> 56),};
    }

    public int getEstimatedCount(byte[] key) {
        long hash64 = Murmur3Util.hash64(key);
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        int min = Integer.MAX_VALUE;
        for (int i = 1; i <= d; i++) {
            int combinedHash = hash1 + (i * hash2);
            // hashcode should be positive, flip all the bits if it's negative
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            int pos = combinedHash % w;
            min = Math.min(min, multiset[i - 1][pos]);
        }

        return min;
    }

    public int getEstimatedCountInt(int val) {
        return getEstimatedCount(intToByteArrayLE(val));
    }
}
