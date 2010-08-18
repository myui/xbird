/**
 * @(#)$Id$
 * 
 * Copyright (c) 2009-2010 Makoto YUI
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
package xbird.util.hashes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;

import javax.annotation.Nullable;

import xbird.util.string.StringUtils;

/**
 * Known hashing algorithms for locating a server for a key.
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
    * 32-bit FNV1.
    */
    FNV1_32_HASH,
    /**
     * 32-bit FNV1a.
     */
    FNV1A_32_HASH,
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
     * 32-bit Jenkins hash
     */
    JENKINS_32_HASH,
    /**
     * 64-bit Jenkins hash
     */
    JENKINS_64_HASH,
    /**
     * 32-bit Murmur hash.
     */
    MURMUR_32_HASH,
    /**
     * 64-bit Murmur hash.
     */
    MURMUR_64_HASH,
    /**
     * JDK's SHA-1 hash
     */
    SHA1_HASH("SHA-1"),
    /**
     * Pure-java SHA-1 hash
     */
    PURE_SHA1_HASH,
    /**
     * MD5-based hash algorithm used by ketama.
     */
    MD5_HASH("MD5");

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private static final long FNV_32_INIT = 2166136261L;
    private static final long FNV_32_PRIME = 16777619;

    private final String algorithmName;
    private final MessageDigest digest;
    private final boolean digestCloneable;

    private HashAlgorithm() {
        this(null);
    }

    private HashAlgorithm(@Nullable String digestAlgorithm) {
        this.algorithmName = digestAlgorithm;
        if(digestAlgorithm != null) {
            digest = newDigest(digestAlgorithm);
            digestCloneable = (digest instanceof Cloneable);
        } else {
            digest = null;
            digestCloneable = false;
        }
    }

    /**
     * Compute the hash for the given key.
     *
     * @return a positive integer hash
     */
    public long hash(final String k) {
        if(digest != null) {
            final byte[] b = StringUtils.getBytes(k);
            final byte[] di = computeMD(digest, b, algorithmName, digestCloneable);
            long rv = ((long) (di[3] & 0xFF) << 24) | ((long) (di[2] & 0xFF) << 16)
                    | ((long) (di[1] & 0xFF) << 8) | (di[0] & 0xFF);
            return rv;
        }
        long rv = 0;
        switch(this) {
            case NATIVE_HASH:
                rv = k.hashCode();
                break;
            case CRC32_HASH: {
                final CRC32 crc32 = new CRC32();
                crc32.update(StringUtils.getBytes(k));
                rv = (crc32.getValue() >> 16) & 0x7fff;
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
            case FNV1_64_HASH: {
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
            case JENKINS_32_HASH: {
                byte[] b = StringUtils.getBytes(k);
                rv = JenkinsHash.hash32(b, 0xdeadbeef);
                break;
            }
            case JENKINS_64_HASH: {
                byte[] b = StringUtils.getBytes(k);
                rv = JenkinsHash.hash64(b, 0x00000000deadbeefL);
                break;
            }
            case MURMUR_32_HASH: {
                byte[] b = StringUtils.getBytes(k);
                rv = MurmurHash.hash32(b, b.length, 0xdeadbeef);
                break;
            }
            case MURMUR_64_HASH: {
                byte[] b = StringUtils.getBytes(k);
                rv = MurmurHash.hash64(b, b.length, 0xdeadbeef);
                break;
            }
            case PURE_SHA1_HASH: {
                SHA1 sha = new SHA1(true);
                byte[] b = StringUtils.getBytes(k);
                sha.update(b);
                sha.finish();
                int v = sha.extract4(0);
                rv = v;
                break;
            }
            default:
                throw new IllegalStateException("Unexpected algorithm: " + this);
        }
        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    public long hash(final byte[] k) {
        if(digest != null) {
            final byte[] di = computeMD(digest, k, algorithmName, digestCloneable);
            long rv = ((long) (di[3] & 0xFF) << 24) | ((long) (di[2] & 0xFF) << 16)
                    | ((long) (di[1] & 0xFF) << 8) | (di[0] & 0xFF);
            return rv;
        }
        long rv = 0;
        switch(this) {
            case NATIVE_HASH:
                rv = Arrays.hashCode(k);
                break;
            case CRC32_HASH: {
                final CRC32 crc32 = new CRC32();
                crc32.update(k);
                rv = (crc32.getValue() >> 16) & 0x7fff;
                break;
            }
            case FNV1_32_HASH: {
                rv = FNV_32_INIT;
                final int len = k.length;
                for(int i = 0; i < len; i++) {
                    rv *= FNV_32_PRIME;
                    rv ^= k[i];
                }
                break;
            }
            case FNV1A_32_HASH: {
                rv = FNV_32_INIT;
                final int len = k.length;
                for(int i = 0; i < len; i++) {
                    rv ^= k[i];
                    rv *= FNV_32_PRIME;
                }
                break;
            }
            case FNV1_64_HASH: {
                rv = FNV_64_INIT;
                final int len = k.length;
                for(int i = 0; i < len; i++) {
                    rv *= FNV_64_PRIME;
                    rv ^= k[i];
                }
                break;
            }
            case FNV1A_64_HASH: {
                rv = FNV_64_INIT;
                final int len = k.length;
                for(int i = 0; i < len; i++) {
                    rv ^= k[i];
                    rv *= FNV_64_PRIME;
                }
                break;
            }
            case JENKINS_32_HASH: {
                rv = JenkinsHash.hash32(k, 0xdeadbeef);
                break;
            }
            case JENKINS_64_HASH: {
                rv = JenkinsHash.hash64(k, 0x00000000deadbeefL);
                break;
            }
            case MURMUR_32_HASH: {
                rv = MurmurHash.hash32(k, k.length, 0xdeadbeef);
                break;
            }
            case MURMUR_64_HASH: {
                rv = MurmurHash.hash64(k, k.length, 0xdeadbeef);
                break;
            }
            case PURE_SHA1_HASH: {
                SHA1 sha = new SHA1(true);
                sha.update(k);
                sha.finish();
                int v = sha.extract4(0);
                rv = v;
                break;
            }
            default:
                throw new IllegalStateException("Unexpected algorithm: " + this);
        }
        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    /**
     * Get a digest of the given key.
     * @param digestCloneable 
     * @param digest 
     */
    private static byte[] computeMD(final MessageDigest digest, final byte[] k, final String algorithm, final boolean digestCloneable) {
        MessageDigest md = digestCloneable ? newDigest(digest) : newDigest(algorithm);
        //md.reset();md.update();
        return md.digest(k);
    }

    private static MessageDigest newDigest(final MessageDigest digest) {
        final MessageDigest md;
        try {
            md = (MessageDigest) digest.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }        
        return md;
    }

    private static MessageDigest newDigest(final String algorithm) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(algorithm + " is not supported", e);
        }
        return md;
    }

    public static HashAlgorithm resolve(final String name) {
        if("sha-1".equalsIgnoreCase(name) || "sha1".equalsIgnoreCase(name)) {
            return HashAlgorithm.SHA1_HASH;
        } else if("jenkins32".equalsIgnoreCase(name)) {
            return HashAlgorithm.JENKINS_32_HASH;
        } else if("jenkins64".equalsIgnoreCase(name)) {
            return HashAlgorithm.JENKINS_64_HASH;
        } else if("murmur32".equalsIgnoreCase(name)) {
            return HashAlgorithm.MURMUR_32_HASH;
        } else if("murmur64".equalsIgnoreCase(name)) {
            return HashAlgorithm.MURMUR_64_HASH;
        } else if("md5".equalsIgnoreCase(name)) {
            return HashAlgorithm.MD5_HASH;
        } else if("native".equalsIgnoreCase(name)) {
            return HashAlgorithm.NATIVE_HASH;
        } else if("crc32".equalsIgnoreCase(name)) {
            return HashAlgorithm.CRC32_HASH;
        } else if("fnv1_32".equalsIgnoreCase(name)) {
            return HashAlgorithm.FNV1_32_HASH;
        } else if("fnv1a_32".equalsIgnoreCase(name)) {
            return HashAlgorithm.FNV1A_32_HASH;
        } else if("fnv1_64".equalsIgnoreCase(name)) {
            return HashAlgorithm.FNV1_64_HASH;
        } else if("fnv1a_64".equalsIgnoreCase(name)) {
            return HashAlgorithm.FNV1A_64_HASH;
        } else if("pure-sha-1".equalsIgnoreCase(name) || "pure-sha1".equalsIgnoreCase(name)) {
            return HashAlgorithm.PURE_SHA1_HASH;
        } else {
            throw new IllegalArgumentException("Unsupported Hash Algrorithm: " + name);
        }
    }

}
