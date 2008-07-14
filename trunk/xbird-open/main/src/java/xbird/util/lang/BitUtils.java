/*
 * @(#)$Id$
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.lang;

import java.io.IOException;

import xbird.util.io.BitInputStream;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BitUtils {

    private static final int BIT_MASK = 0x7;

    private BitUtils() {}

    public static int getBits(final byte[] bytes, final int bitOffset, final int bitLen) {
        assert (bitLen <= 32) : bitLen;
        int result = 0;
        final int byteslen = bytes.length;
        final long limit = bitOffset + bitLen;
        for(int bi = bitOffset; bi < limit; bi++) {
            final int quot = bi >>> 3; // b / 8
            if(quot >= byteslen) {
                throw new ArrayIndexOutOfBoundsException();
            }
            final byte b = bytes[quot];
            final int rem = bi & 7; // b % 8
            final int mask = 1 << rem;
            if((b & mask) != 0) {
                result |= 1 << (bi - bitOffset);
            }
        }
        return result;
    }

    public static long getLongBits(final byte[] bytes, final long bitOffset, final long bitLen) {
        assert (bitLen <= 64) : bitLen;
        long result = 0;
        final int byteslen = bytes.length;
        final long last = bitOffset + bitLen - 1;
        for(long bi = bitOffset; bi <= last; bi++) {
            final long lquot = bi >>> 3; // b / 8
            assert (lquot <= 0x7fffffffL) : "Illegal quot: " + lquot;
            final int quot = (int) lquot;
            if(quot >= byteslen) {
                throw new ArrayIndexOutOfBoundsException("quot(" + quot + ") >= bytes.length("
                        + byteslen + ')');
            }
            final byte b = bytes[quot];
            final int rem = (int) (bi & 7); // b % 8
            final int mask = 0x80 >> rem;
            if((b & mask) != 0) {
                result |= 1L << (last - bi);
            }
        }
        return result;
    }

    public static long getLongBits(final BitInputStream in, final long bitLen) throws IOException {
        assert (bitLen <= 64) : bitLen;
        long result = 0;
        final long last = bitLen - 1;
        for(long bi = 0; bi <= last; bi++) {
            final boolean frag = in.readBit();
            if(frag) {
                result |= 1L << (last - bi);
            }
        }
        return result;
    }

    /**
     * Returns the index of the most significant bit in state "true".
     * Returns -1 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 30
     *  0x00000001 --> 0
     *  0x00000000 --> -1
     * </pre>
     */
    public static int mostSignificantBit(final int value) {
        int i = 32;
        while(--i >= 0 && (((1 << i) & value)) == 0)
            ;
        return i;
    }

    /**
     * Returns the index of the most significant bit in state "true".
     * Returns -1 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 30
     *  0x00000001 --> 0
     *  0x00000000 --> -1
     * </pre>
     */
    public static int mostSignificantBit(final byte value) {
        int i = 8;
        while(--i >= 0 && (((1 << i) & value)) == 0)
            ;
        return i;
    }

    /**
     * Returns the index of the most significant bit in state "true".
     * Returns -1 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 30
     *  0x00000001 --> 0
     *  0x00000000 --> -1
     * </pre>
     */
    public static int mostSignificantBit(final long value) {
        int i = 64;
        while(--i >= 0 && (((1L << i) & value)) == 0)
            ;
        return i;
    }

    /**
     * Returns the index of the least significant bit in state "true".
     * Returns 32 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 0
     *  0x00000001 --> 0
     *  0x00000000 --> 32
     * </pre>
     */
    public static int leastSignificantBit(final int value) {
        int i = -1;
        while(++i < 32 && (((1 << i) & value)) == 0)
            ;
        return i;
    }

    /**
     * Returns the index of the least significant bit in state "true".
     * Returns 64 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 0
     *  0x00000001 --> 0
     *  0x00000000 --> 32
     * </pre>
     */
    public static int leastSignificantBit(final long value) {
        int i = -1;
        while(++i < 64 && (((1 << i) & value)) == 0)
            ;
        return i;
    }

    /**
     * Returns the index of the least significant bit in state "true".
     * Returns 8 if no bit is in state "true".
     * <pre>
     * Examples:
     *  0x80000000 --> 31
     *  0x7fffffff --> 0
     *  0x00000001 --> 0
     *  0x00000000 --> 32
     * </pre>
     */
    public static int leastSignificantBit(final byte value) {
        int i = -1;
        while(++i < 8 && (((1 << i) & value)) == 0)
            ;
        return i;
    }

    public static boolean isBitSet(final byte[] bits, final int bitIndex) {
        final int byteIndex = bitIndex >>> 3;
        if(byteIndex >= bits.length) {
            return false;
        }
        return (bits[byteIndex] & (1 << (bitIndex & BIT_MASK))) != 0;
    }

    public static boolean isBitSet(final byte b, final int bitIndex) {
        assert (bitIndex < 8);
        return (b & (1 << bitIndex)) != 0;
    }

    public static boolean startsWith(final byte[] target, final byte[] prefix, final int bits) {
        if(bits == 0) {
            return true;
        }
        final int bytes = bits >>> 3;
        if(bytes < prefix.length) {
            throw new IllegalArgumentException("prefix does not have " + bits + " bits: "
                    + prefix.length + " bytes");
        }
        int cmp = ArrayUtils.compareTo(target, prefix, bytes);
        if(cmp != 0) {
            return false;
        }
        final int lastBits = bits & BIT_MASK;
        for(int i = 0; i < lastBits; i++) {
            if(isBitSet(target, i) != isBitSet(prefix, i)) {
                return false;
            }
        }
        return true;
    }

    public static String toString(final byte[] b, final int offset, final int len) {
        final StringBuilder buf = new StringBuilder(len * 2);
        for(int i = offset; i < len; i++) {
            if(i == offset) {
                int topad = i % 8;
                for(int j = 0; j < topad; j++) {
                    buf.append(' ');
                }
            } else if((i % 8) == 0) {
                buf.append(',');
            }
            buf.append(isBitSet(b, i) ? '1' : '0');
        }
        return buf.toString();
    }

    public static String toString(final byte[] b, final int offset, final int len, final int bit, final char decorate) {
        final StringBuilder buf = new StringBuilder(len * 2);
        final int lastbyte = (bit >>> 3) << 3;
        for(int i = offset; i < len; i++) {
            if(i != offset && ((i % 8) == 0)) {
                buf.append(',');
            }
            if(i < lastbyte) {
                final int bi = i >>> 3;
                if(bi < b.length) {
                    StringUtils.encodeHex(b[i >>> 3], buf);
                } else {
                    buf.append("00");
                }
                i += 7;
                continue;
            }
            if(i == bit) {
                buf.append(decorate);
                buf.append(isBitSet(b, i) ? '1' : '0');
                buf.append(decorate);
                break;
            } else {
                buf.append(isBitSet(b, i) ? '1' : '0');
            }
        }
        return buf.toString();
    }
}
