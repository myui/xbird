/**
 * @(#)$Id$
 * 
 * Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xbird.util.lang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import xbird.util.string.StringUtils;

/**
 * Known hashing algorithms for locating a server for a key.
 * Note that all hash algorithms return 64-bits of hash, but only the lower
 * 32-bits are significant.  This allows a positive 32-bit number to be
 * returned for all cases.
 */
public enum HashAlgorithm {

    /**
     * Native hash (String.hashCode()).
     */
    NATIVE_HASH,
    /**
     * CRC32_HASH as used by the perl API. This will be more consistent both
     * across multiple API users as well as java versions, but is mostly likely
     * significantly slower.
     */
    CRC32_HASH,
    /**
     * FNV hashes are designed to be fast while maintaining a low collision
     * rate. The FNV speed allows one to quickly hash lots of data while
     * maintaining a reasonable collision rate.
     *
     * @see http://www.isthe.com/chongo/tech/comp/fnv/
     * @see http://en.wikipedia.org/wiki/Fowler_Noll_Vo_hash
     */
    FNV1_64_HASH,
    /**
     * Variation of FNV.
     */
    FNV1A_64_HASH,
    /**
     * 32-bit FNV1.
     */
    FNV1_32_HASH,
    /**
     * 32-bit FNV1a.
     */
    FNV1A_32_HASH,
    /**
     * SHA-1 hash
     */
    SHA1_HASH,
    /**
     * MD5-based hash algorithm used by ketama.
     */
    MD5_HASH;

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private static final long FNV_32_INIT = 2166136261L;
    private static final long FNV_32_PRIME = 16777619;

    /**
     * Compute the hash for the given key.
     *
     * @return a positive integer hash
     */
    public long hash(final String k) {
        long rv = 0;
        switch(this) {
            case NATIVE_HASH:
                rv = k.hashCode();
                break;
            case CRC32_HASH: {
                // return (crc32(shift) >> 16) & 0x7fff;
                final CRC32 crc32 = new CRC32();
                crc32.update(StringUtils.getBytes(k));
                rv = (crc32.getValue() >> 16) & 0x7fff;
                break;
            }
            case FNV1_64_HASH: {
                // Thanks to pierre@demartines.com for the pointer
                rv = FNV_64_INIT;
                final int len = k.length();
                for(int i = 0; i < len; i++) {
                    rv *= FNV_64_PRIME;
                    rv ^= k.charAt(i);
                }
                break;
            }
            case FNV1A_64_HASH: {
                rv = FNV_64_INIT;
                final int len = k.length();
                for(int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_64_PRIME;
                }
                break;
            }
            case FNV1_32_HASH: {
                rv = FNV_32_INIT;
                final int len = k.length();
                for(int i = 0; i < len; i++) {
                    rv *= FNV_32_PRIME;
                    rv ^= k.charAt(i);
                }
                break;
            }
            case FNV1A_32_HASH: {
                rv = FNV_32_INIT;
                final int len = k.length();
                for(int i = 0; i < len; i++) {
                    rv ^= k.charAt(i);
                    rv *= FNV_32_PRIME;
                }
                break;
            }
            case SHA1_HASH: {
                final byte[] bKey = computeMD(k, "SHA-1");
                rv = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16)
                        | ((long) (bKey[1] & 0xFF) << 8) | (bKey[0] & 0xFF);
                break;
            }
            case MD5_HASH: {
                final byte[] bKey = computeMD(k, "MD5");
                rv = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16)
                        | ((long) (bKey[1] & 0xFF) << 8) | (bKey[0] & 0xFF);
                break;
            }
            default:
                assert false;
        }
        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    /**
     * Get the md5 of the given key.
     */
    public static byte[] computeMD(final String k, final String algorithm) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + " is not supported.", e);
        }
        md.reset();
        md.update(StringUtils.getBytes(k));
        return md.digest();
    }

}
