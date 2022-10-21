package com.ads.mini_project.data;

import com.ads.mini_project.util.Murmur3Util;
import lombok.extern.slf4j.Slf4j;

import java.util.BitSet;

@Slf4j
public class BloomFilter {

    private static final int MAX_HASHES = 8;
    private static final long[] byteTable;
    private static final long HSTART = 0xBB40E64DA205B064L;
    private static final long HMULT = 7664345821815920749L;

    private final BitSet data;
    private final int noHashes;
    private final int hashMask;

    static {
        byteTable = new long[256 * MAX_HASHES];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < byteTable.length; i++) {
            for (int j = 0; j < 31; j++)
                h = (h >>> 7) ^ h; h = (h << 11) ^ h; h = (h >>> 10) ^ h;
            byteTable[i] = h;
        }
    }

    public BloomFilter(int log2noBits, int noHashes) {
        if (log2noBits < 1 || log2noBits > 31)
            throw new IllegalArgumentException("Invalid number of bits");
        if (noHashes < 1 || noHashes > MAX_HASHES)
            throw new IllegalArgumentException("Invalid number of hashes");

        this.data = new BitSet(1 << log2noBits);
        this.noHashes = noHashes;
        this.hashMask = (1 << log2noBits) - 1;
    }

    public void add(String key) {
        for (int n = 0; n < noHashes; n++) {
            long hc = hashCode(key, n);
            int bitNo = (int) (hc) & this.hashMask;
            data.set(bitNo);
        }
    }

    public boolean contains(String key) {
        for (int n = 0; n < noHashes; n++) {
            long hc = hashCode(key, n);
            int bitNo = (int) (hc) & this.hashMask;
            if (!data.get(bitNo)) return false;
        }
        return true;
    }

    private long hashCode(String s, int hcNo) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        int startIx = 256 * hcNo;
        for (int len = s.length(), i = 0; i < len; i++) {
            char ch = s.charAt(i);
            h = (h * hmult) ^ ht[startIx + (ch & 0xff)];
            h = (h * hmult) ^ ht[startIx + ((ch >>> 8) & 0xff)];
        }
        return h;
    }
}
