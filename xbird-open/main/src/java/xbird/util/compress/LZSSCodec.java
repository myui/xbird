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
package xbird.util.compress;

import xbird.util.lang.Primitives;

/**
 * LZSS codec with simplified sunday's Quick Search (a fast BMSearch).
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www-igm.univ-mlv.fr/~lecroq/string/node19.html
 */
public final class LZSSCodec implements CompressionCodec {

    private static final int WINDOW_SIZE = 4096;
    private static final int MAX_MATCH_LENGTH = 18;
    private static final int THRESHOLD = 3;
    private static final int MAX_INPUT_BYTES = 16777216; /* 0xffffff */

    public byte[] compress(final byte[] input) {
        return compress(input, input.length);
    }

    public static byte[] compress(final byte[] input, final int size) {
        final int inSize = size;
        if(inSize == 0 || inSize > MAX_INPUT_BYTES) {
            throw new IllegalArgumentException("illegal input size: " + inSize);
        }
        final byte[] compressed = new byte[/* flag */((inSize * 9) >> 3) + 1 + /* length */3];

        // controls
        int flags = 3, data = 4, window = 0, index = 0;
        int mask = 128; // 1000 0000

        // original data length
        compressed[0] = (byte) inSize;
        compressed[1] = (byte) (inSize >> 8);
        compressed[2] = (byte) (inSize >> 16);

        compressed[flags] = 0;
        compressed[data++] = input[index++];

        final int[] skip = makeSkipTable(input);

        while(index < inSize) {
            mask >>= 1;
            if(mask == 0) {
                mask = 128;
                flags = data++;
                compressed[flags] = 0;
            }
            window = (index < WINDOW_SIZE) ? 0 : index - WINDOW_SIZE;

            final Match matched = quickSearch(input, window, index, skip);
            //final Match matched = bruteForceSearch(input, window, index);
            final int matchlen = matched.length;
            if(matchlen >= THRESHOLD) {
                compressed[flags] |= mask;
                int displacement = index - matched.index - 1;
                compressed[data++] = (byte) (((matchlen - THRESHOLD) << 4) | ((displacement >>> 8) & 0x0f));
                compressed[data++] = (byte) displacement;
                index += matchlen;
            } else {
                compressed[data++] = input[index++];
            }
        }

        final byte[] res = new byte[data];
        System.arraycopy(compressed, 0, res, 0, data);
        return res;
    }

    public byte[] decompress(final byte[] in) {
        return decompress(in, 0, in.length);
    }

    public static byte[] decompress(final byte[] in, final int off, final int limit) {
        int size = ((in[off + 2] & 0xff) << 16) + ((in[off + 1] & 0xff) << 8)
                + (in[off] & 0xff);
        final byte[] buf = new byte[size];
        int bufIndex = 0;
        int inputPtr = 3;
        while(size > 0) {
            final int flags = in[inputPtr++];
            for(int mask = 128; mask != 0 && size > 0; mask >>= 1) {
                if((flags & mask) != 0) {//is compressed?
                    final int d1 = in[inputPtr++];
                    final int d2 = in[inputPtr++];
                    final int matchlen = ((d1 >>> 4) & 0x0f) + THRESHOLD;
                    final int displacement = ((d1 & 0x0f) << 8) + (d2 & 0xff) + 1;
                    final int dstFrom = bufIndex - displacement;
                    for(int k = 0; k < matchlen; k++) {
                        buf[bufIndex + k] = buf[dstFrom + k];
                    }
                    //System.arraycopy(buf, bufIndex - displacement, buf, bufIndex, matchlen);
                    bufIndex += matchlen;
                    size -= matchlen;
                } else {
                    size--;
                    buf[bufIndex++] = in[inputPtr++];
                }
            }
        }
        return buf;
    }

    public char[] decompressAsChars(final byte[] input) {        
        final int buflen = ((input[2] & 0xff) << 16) + ((input[1] & 0xff) << 8) + (input[0] & 0xff);
        final byte[] buf = new byte[buflen];
        int bufIndex = 0;
        int inputPtr = 3;
        int size = buflen;
        while(size > 0) {
            final int flags = input[inputPtr++];
            for(int mask = 128; mask != 0 && size > 0; mask >>= 1) {
                if((flags & mask) != 0) {//is compressed?
                    final int d1 = input[inputPtr++];
                    final int d2 = input[inputPtr++];
                    final int matchlen = ((d1 >>> 4) & 0x0f) + THRESHOLD;
                    final int displacement = ((d1 & 0x0f) << 8) + (d2 & 0xff) + 1;
                    final int dstFrom = bufIndex - displacement;
                    for(int k = 0; k < matchlen; k++) {
                        buf[bufIndex + k] = buf[dstFrom + k];
                    }
                    bufIndex += matchlen;
                    size -= matchlen;
                } else {
                    size--;
                    buf[bufIndex++] = input[inputPtr++];
                }
            }
        }
        return Primitives.toChars(buf, 0, size);
    }

    @Deprecated
    private static Match bruteForceSearch(final byte[] pattern, final int windowPos, final int lastDataIdx) {
        final int limit = pattern.length;
        final Match result = new Match(pattern);
        for(int i = windowPos; i < lastDataIdx; i++) {
            int count = 0;
            while(count < MAX_MATCH_LENGTH && lastDataIdx + count < limit) {
                if(pattern[i + count] != pattern[lastDataIdx + count]) {
                    break;
                } else {
                    count++;
                }
            }
            if(count > result.length) {
                result.length = count;
                result.index = i;
            }
            if(count == MAX_MATCH_LENGTH) {
                break;
            }
        }
        return result;
    }

    /**
     * An implementation of Sunday's simplified "Quick Finder" version of the
     * Boyer-Moore algorithm. See "A very fast substring search algorithm" (appeared
     * in <em>Communications of the ACM . 33 (8):132-142</em>).
     * <pre>
     * Preprocessing: O(m + &sum;) time
     * Processing   : O(mn) worst case
     * </pre>
     */
    private static Match quickSearch(final byte[] pattern, final int windowPos, final int lastDataIdx, final int[] skip) {
        final int limit = pattern.length;
        final Match result = new Match();
        int i = windowPos;
        while(i < lastDataIdx) {
            int count = 0;
            while(count < MAX_MATCH_LENGTH && lastDataIdx + count < limit) {
                if(pattern[i + count] != pattern[lastDataIdx + count]) {
                    break;
                } else {
                    count++;
                }
            }
            if(count > result.length) {
                result.length = count;
                result.index = i;
            }
            if(count == MAX_MATCH_LENGTH) {
                break;
            } else {
                int skipIdx = index(pattern[i + count]);
                i += skip[skipIdx];
            }
        }
        return result;
    }

    private static int[] makeSkipTable(final byte[] pattern) {
        final int[] skip = new int[256];
        final int ptnlen = pattern.length;
        for(int i = 0; i < 256; ++i) {
            skip[i] = ptnlen + 1;
        }
        for(int i = 0; i < ptnlen; ++i) {
            skip[index(pattern[i])] = ptnlen - i;
        }
        return skip;
    }

    /**
     * Converts the given <code>byte</code> to an <code>int</code>.
     */
    private static int index(final byte idx) {
        return (idx < 0) ? 256 + idx : idx;
    }

    private static final class Match {
        private final byte[] pattern;
        int index = -1;
        int length = 0;

        public Match() {
            this.pattern = null;
        }

        public Match(final byte[] pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            if(index == -1) {
                return null;
            } else {
                return new String(pattern, index, length);
            }
        }
    }

}
