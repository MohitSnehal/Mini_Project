package com.ads.mini_project.util;

import com.ads.mini_project.data.HyperLogLogV2;
import com.ads.mini_project.data.HyperLogLogV2.EncodingType;
import org.apache.hadoop.hive.common.ndv.hll.HLLConstants;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * HyperLogLog serialization utilities.
 * Code Reference: https://github.com/apache/hive/blob/master/standalone-metastore/
 */
public class HyperLogLogUtils {

    public static final byte[] MAGIC = new byte[] { 'H', 'L', 'L' };
    public static void serializeHLL(OutputStream out, HyperLogLogV2 hll) throws IOException {

        // write header
        out.write(MAGIC);
        int fourthByte = 0;
        int p = hll.getNumRegisterIndexBits();
        fourthByte = (p & 0xff) << 4;

        int bitWidth = 0;
        EncodingType enc = hll.getEncoding();

        // determine bit width for bitpacking and encode it in header
        if (enc.equals(HyperLogLogV2.EncodingType.DENSE)) {
            int lzr = hll.getHLLDenseRegister().getMaxRegisterValue();
            bitWidth = getBitWidth(lzr);

            // the max value of number of zeroes for 64 bit hash can be encoded using
            // only 6 bits. So we will disable bit packing for any values >6
            if (bitWidth > 6) {
                fourthByte |= 7;
                bitWidth = 8;
            } else {
                fourthByte |= (bitWidth & 7);
            }
        }

        // write fourth byte of header
        out.write(fourthByte);

        // write estimated count
        long estCount = hll.estimateNumDistinctValues();
        writeVulong(out, estCount);

        // serialize dense/sparse registers. Dense registers are bitpacked whereas
        // sparse registers are delta and variable length encoded
        if (enc.equals(EncodingType.DENSE)) {
            byte[] register = hll.getHLLDenseRegister().getRegister();
            bitpackHLLRegister(out, register, bitWidth);
        } else if (enc.equals(EncodingType.SPARSE)) {
            Map<Integer, Byte> sparseMap = hll.getHLLSparseRegister().getSparseMap();

            // write the number of elements in sparse map (required for
            // reconstruction)
            writeVulong(out, sparseMap.size());

            // compute deltas and write the values as varints
            int prev = 0;
            for (Map.Entry<Integer, Byte> entry : sparseMap.entrySet()) {
                if (prev == 0) {
                    prev = (entry.getKey() << HLLConstants.Q_PRIME_VALUE) | entry.getValue();
                    writeVulong(out, prev);
                } else {
                    int curr = (entry.getKey() << HLLConstants.Q_PRIME_VALUE) | entry.getValue();
                    int delta = curr - prev;
                    writeVulong(out, delta);
                    prev = curr;
                }
            }
        }
    }

    /**
     * Refer serializeHLL() for format of serialization. This function
     * deserializes the serialized hyperloglogs
     * @param in
     *          - input stream
     * @return deserialized hyperloglog
     * @throws IOException
     */
    public static HyperLogLogV2 deserializeHLL(InputStream in) throws IOException {
        checkMagicString(in);
        int fourthByte = in.read() & 0xff;
        int p = fourthByte >>> 4;

        // read type of encoding
        int enc = fourthByte & 7;
        EncodingType encoding = null;
        int bitSize = 0;
        if (enc == 0) {
            encoding = EncodingType.SPARSE;
        } else if (enc > 0 && enc < 7) {
            bitSize = enc;
            encoding = EncodingType.DENSE;
        } else {
            // bit packing disabled
            bitSize = 8;
            encoding = EncodingType.DENSE;
        }

        // estimated count
        long estCount = readVulong(in);

        HyperLogLogV2 result = null;
        if (encoding.equals(EncodingType.SPARSE)) {
            result = HyperLogLogV2.builder().setNumRegisterIndexBits(p)
                    .setEncoding(EncodingType.SPARSE).build();
            int numRegisterEntries = (int) readVulong(in);
            int[] reg = new int[numRegisterEntries];
            int prev = 0;

            // reconstruct the sparse map from delta encoded and varint input stream
            if (numRegisterEntries > 0) {
                prev = (int) readVulong(in);
                reg[0] = prev;
            }
            int delta = 0;
            int curr = 0;
            for (int i = 1; i < numRegisterEntries; i++) {
                delta = (int) readVulong(in);
                curr = prev + delta;
                reg[i] = curr;
                prev = curr;
            }
            result.setHLLSparseRegister(reg);
        } else {

            // explicitly disable bit packing
            if (bitSize == 8) {
                result = HyperLogLogV2.builder().setNumRegisterIndexBits(p)
                        .setEncoding(EncodingType.DENSE).enableBitPacking(false).build();
            } else {
                result = HyperLogLogV2.builder().setNumRegisterIndexBits(p)
                        .setEncoding(EncodingType.DENSE).enableBitPacking(true).build();
            }
            int m = 1 << p;
            byte[] register = unpackHLLRegister(in, m, bitSize);
            result.setHLLDenseRegister(register);
        }

        result.setCount(estCount);

        return result;
    }

    /**
     * This function deserializes the serialized hyperloglogs from a byte array.
     * @param buf
     * @param start
     * @param len
     * @return
     */
    public static HyperLogLogV2 deserializeHLL(final byte[] buf, final int start, final int len) {
        InputStream is = new ByteArrayInputStream(buf, start, len); // TODO: use faster non-sync inputstream
        try {
            HyperLogLogV2 result = deserializeHLL(is);
            is.close();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This function deserializes the serialized hyperloglogs from a byte array.
     * @param buf - to deserialize
     * @return HyperLogLog
     */
    public static HyperLogLogV2 deserializeHLL(final byte[] buf) {
        return deserializeHLL(buf, 0, buf.length);
    }

    private static void bitpackHLLRegister(OutputStream out, byte[] register, int bitWidth)
            throws IOException {
        int bitsLeft = 8;
        byte current = 0;

        if (bitWidth == 8) {
            fastPathWrite(out, register);
            return;
        }

        // write the blob
        for (byte value : register) {
            int bitsToWrite = bitWidth;
            while (bitsToWrite > bitsLeft) {
                // add the bits to the bottom of the current word
                current |= value >>> (bitsToWrite - bitsLeft);
                // subtract out the bits we just added
                bitsToWrite -= bitsLeft;
                // zero out the bits above bitsToWrite
                value &= (1 << bitsToWrite) - 1;
                out.write(current);
                current = 0;
                bitsLeft = 8;
            }
            bitsLeft -= bitsToWrite;
            current |= value << bitsLeft;
            if (bitsLeft == 0) {
                out.write(current);
                current = 0;
                bitsLeft = 8;
            }
        }

        out.flush();
    }

    private static void fastPathWrite(OutputStream out, byte[] register) throws IOException {
        for (byte b : register) {
            out.write(b);
        }
    }

    /**
     * Unpack the bitpacked HyperLogLog register.
     * @param in
     *          - input stream
     * @param length
     *          - serialized length
     * @return unpacked HLL register
     * @throws IOException
     */
    private static byte[] unpackHLLRegister(InputStream in, int length, int bitSize)
            throws IOException {
        int mask = (1 << bitSize) - 1;
        int bitsLeft = 8;

        if (bitSize == 8) {
            return fastPathRead(in, length);
        }

        byte current = (byte) (0xff & in.read());

        byte[] output = new byte[length];
        for (int i = 0; i < output.length; i++) {
            byte result = 0;
            int bitsLeftToRead = bitSize;
            while (bitsLeftToRead > bitsLeft) {
                result <<= bitsLeft;
                result |= current & ((1 << bitsLeft) - 1);
                bitsLeftToRead -= bitsLeft;
                current = (byte) (0xff & in.read());
                bitsLeft = 8;
            }
            if (bitsLeftToRead > 0) {
                result <<= bitsLeftToRead;
                bitsLeft -= bitsLeftToRead;
                result |= (current >>> bitsLeft) & ((1 << bitsLeftToRead) - 1);
            }
            output[i] = (byte) (result & mask);
        }
        return output;
    }

    private static byte[] fastPathRead(InputStream in, int length) throws IOException {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) in.read();
        }
        return result;
    }

    /**
     * Get estimated cardinality without deserializing HLL
     * @param in
     *          - serialized HLL
     * @return - cardinality
     * @throws IOException
     */
    public static long getEstimatedCountFromSerializedHLL(InputStream in) throws IOException {
        checkMagicString(in);
        in.read();
        return readVulong(in);
    }

    /**
     * Check if the specified input stream is actually a HLL stream
     * @param in
     *          - input stream
     * @throws IOException
     */
    private static void checkMagicString(InputStream in) throws IOException {
        byte[] magic = new byte[3];
        magic[0] = (byte) in.read();
        magic[1] = (byte) in.read();
        magic[2] = (byte) in.read();

        if (!Arrays.equals(magic, MAGIC)) {
            throw new IllegalArgumentException("The input stream is not a HyperLogLog stream.");
        }
    }

    /**
     * Minimum bits required to encode the specified value
     * @param val
     *          - input value
     * @return
     */
    private static int getBitWidth(int val) {
        int count = 0;
        while (val != 0) {
            count++;
            val = (byte) (val >>> 1);
        }
        return count;
    }

    /**
     * Return relative error between actual and estimated cardinality
     * @param actualCount
     *          - actual count
     * @param estimatedCount
     *          - estimated count
     * @return relative error
     */
    public static float getRelativeError(long actualCount, long estimatedCount) {
        float err = (1.0f - ((float) estimatedCount / (float) actualCount)) * 100.0f;
        return err;
    }

    /**
     * Write variable length encoded longs to output stream
     * @param output
     *          - out stream
     * @param value
     *          - long
     * @throws IOException
     */
    private static void writeVulong(OutputStream output, long value) throws IOException {
        while (true) {
            if ((value & ~0x7f) == 0) {
                output.write((byte) value);
                return;
            } else {
                output.write((byte) (0x80 | (value & 0x7f)));
                value >>>= 7;
            }
        }
    }

    private static long readVulong(InputStream in) throws IOException {
        long result = 0;
        long b;
        int offset = 0;
        do {
            b = in.read();
            if (b == -1) {
                throw new EOFException("Reading Vulong past EOF");
            }
            result |= (0x7f & b) << offset;
            offset += 7;
        } while (b >= 0x80);
        return result;
    }

}