package com.ads.mini_project.data;

import com.ads.mini_project.util.Murmur3Util;

import java.nio.charset.StandardCharsets;

public class HyperLogLog {
    private long registers[];
    private int m; //number of registers
    private long b;

    public HyperLogLog(int m) {
        this.m = m;
        registers = new long[m];
        b = (long) Math.ceil(Math.log((double) m));
    }

    public void add(String s) {
        long hash = Murmur3Util.hash64(s.getBytes(StandardCharsets.UTF_8));
        long k = 64 - this.b; // first b bits
        long r = leftmostActiveBit(hash << this.b);
        int j = (int) (hash >> k);

        if(r > registers[j]) {
            registers[j] = r;
        }
    }

    long leftmostActiveBit(Long x) {
        return 1 + Long.numberOfLeadingZeros(x);
    }

    public long count() {
        long sum = 0;
        double m = (double) this.m;
        for(long val: registers) {
            sum += Math.pow(Math.pow(2, (double)(val)),-1);
        }
        double estimate = (.79402 * m * m ) / sum;
        return (long)(estimate);
    }
}
