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
package xbird.util.hashes;

/**
 * This is a very fast, non-cryptographic hash suitable for general hash-based
 * lookup.  See http://murmurhash.googlepages.com/ for more details.
 * 
 * <p>The C version of MurmurHash 2.0 found at that site was ported
 * to Java by Andrzej Bialecki (ab at getopt org).</p>
 */
public final class MurmurHash {

    private final int seed;

    public MurmurHash() {
        this(0xdeadbeef);
    }

    public MurmurHash(int seed) {
        this.seed = seed;
    }

    public int hash32(final byte[] data) {
        return hash32(data, data.length, seed);
    }

    public int hash32(final byte[] data, final int length) {
        return hash32(data, length, seed);
    }

    public static int hash32(final byte[] data, final int length, final int seed) {
        final int m = 0x5bd1e995;
        final int r = 24;

        int h = seed ^ length;

        final int len_4 = length >> 2;

        for(int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // avoid calculating modulo
        final int len_m = len_4 << 2;
        final int left = length - len_m;

        if(left != 0) {
            if(left >= 3) {
                h ^= (int) data[length - 3] << 16;
            }
            if(left >= 2) {
                h ^= (int) data[length - 2] << 8;
            }
            if(left >= 1) {
                h ^= (int) data[length - 1];
            }

            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }

    public long hash64(final byte[] data) {
        return hash64(data, data.length, seed);
    }

    public long hash64(final byte[] data, final int seed) {
        return hash64(data, data.length, seed);
    }

    public static long hash64(final byte[] data, final int length, final int seed) {
        final long m = 0xc6a4a7935bd1e995L;
        final int r = 47;

        long h = seed ^ (length * m);

        final int len_8 = length >> 3;

        int i_8 = 0;
        for(int i = 0; i < len_8; i++) {
            i_8 = i << 3;
            long k = getLongFromBytes(data, i_8);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        final int idx = i_8 + 8;
        final int left = length & 7;

        for(int i = 0, offset = (left - 1) << 3; i < left; ++i, offset -= 8) {
            h ^= data[idx + i] << offset;
        }

        h ^= h >> r;
        h *= m;
        h ^= h >> r;

        return h;
    }

    private static long getLongFromBytes(final byte[] data, final int offset) {
        long ret = data[offset];
        for(int i = 1; i < 8; ++i) {
            ret = ret << 8;
            ret = ret | (data[offset + i] & 0xff);
        }
        return ret;
    }

}