/*
 * Copyright (c) 2005-2006 Makoto YUI
 * Copyright (c) 2004-2006 H2 Group. Licensed under the H2 License, Version 1.0 (http://h2database.com/html/license.html).
 * Copyright (c) 2000-2005 Marc Alexander Lehmann <schmorp@schmorp.de>
 * Copyright (c) 2005 Oren J. Maurice <oymaurice@hazorea.org.il>
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 * 
 *   1.  Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 * 
 *   2.  Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 * 
 *   3.  The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MER-
 * CHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPE-
 * CIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTH-
 * ERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Contributors:
 *     Makoto YUI - some modification and bug fixes
 */
package xbird.util.compress;

import xbird.util.primitive.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.goof.com/pcg/marc/liblzf.html
 */
public final class LZFCodec implements CompressionCodec {

    private static final int HASH_SIZE = (1 << 14);
    private static final int MAX_LITERAL = (1 << 5);
    private static final int MAX_OFF = (1 << 13);
    private static final int MAX_REF = ((1 << 8) + (1 << 3));

    public LZFCodec() {}

    public byte[] compress(final byte[] in) {
        return compress(in, in.length);
    }

    public byte[] compress(final byte[] in, final int inLength) {
        final byte[] out = new byte[inLength * 3 >> 1];
        final int size = compress(in, inLength, out, 0);
        final byte[] dst = new byte[size + 3];
        dst[0] = (byte) inLength;
        dst[1] = (byte) (inLength >> 8);
        dst[2] = (byte) (inLength >> 16);
        System.arraycopy(out, 0, dst, 3, size);
        return dst;
    }

    public static int compress(final byte[] in, final int inLen, final byte[] out, int outPos) {
        int inPos = 0;
        final int[] hashTab = new int[HASH_SIZE];
        int literals = 0;
        int hval = first(in, inPos);
        while(true) {
            if(inPos < inLen - 4) {
                hval = next(hval, in, inPos);
                int off = hash(hval);
                int ref = hashTab[off];
                hashTab[off] = inPos;
                off = inPos - ref - 1;
                if(off < MAX_OFF && ref > 0 && in[ref + 2] == in[inPos + 2]
                        && in[ref + 1] == in[inPos + 1] && in[ref] == in[inPos]) {
                    int maxlen = inLen - inPos - 2;
                    maxlen = maxlen > MAX_REF ? MAX_REF : maxlen;
                    int len = 3;
                    while(len < maxlen && in[ref + len] == in[inPos + len]) {
                        len++;
                    }
                    len -= 2;
                    if(literals != 0) {
                        out[outPos++] = (byte) (literals - 1);
                        literals = -literals;
                        do {
                            out[outPos++] = in[inPos + literals++];
                        } while(literals != 0);
                    }
                    if(len < 7) {
                        out[outPos++] = (byte) ((off >> 8) + (len << 5));
                    } else {
                        out[outPos++] = (byte) ((off >> 8) + (7 << 5));
                        out[outPos++] = (byte) (len - 7);
                    }
                    out[outPos++] = (byte) off;
                    inPos += len;
                    hval = first(in, inPos);
                    hval = next(hval, in, inPos);
                    hashTab[hash(hval)] = inPos++;
                    hval = next(hval, in, inPos);
                    hashTab[hash(hval)] = inPos++;
                    continue;
                }
            } else if(inPos == inLen) {
                break;
            }
            inPos++;
            literals++;
            if(literals == MAX_LITERAL) {
                out[outPos++] = (byte) (literals - 1);
                literals = -literals;
                do {
                    out[outPos++] = in[inPos + literals++];
                } while(literals != 0);
            }
        }
        if(literals != 0) {
            out[outPos++] = (byte) (literals - 1);
            for(int i = inPos - literals; i != inPos; i++) {
                out[outPos++] = in[i];
            }
        }
        return outPos;
    }

    public byte[] decompress(final byte[] in) {
        final int size = ((in[2] & 0xff) << 16) + ((in[1] & 0xff) << 8) + (in[0] & 0xff);
        final byte[] dst = new byte[size];
        decompress(in, 3, dst, 0, size);
        return dst;
    }

    public char[] decompressAsChars(final byte[] in) {
        final int size = ((in[2] & 0xff) << 16) + ((in[1] & 0xff) << 8) + (in[0] & 0xff);
        final byte[] dst = new byte[size];
        decompress(in, 3, dst, 0, size);
        return Primitives.toChars(dst, 0, size);
    }

    public static void decompress(final byte[] in, int inPos, final byte[] out, int outPos, final int outLength) {
        do {
            final int ctrl = in[inPos++] & 255;
            if(ctrl < MAX_LITERAL) {
                // literal run
                for(int i = 0; i <= ctrl; i++) {
                    out[outPos++] = in[inPos++];
                }
            } else {
                // back reference
                int len = ctrl >> 5;
                int ref = outPos - ((ctrl & 0x1f) << 8) - 1;
                if(len == 7) {
                    len += in[inPos++] & 255;
                }
                ref -= in[inPos++] & 255;
                len += outPos + 2;
                out[outPos++] = out[ref++];
                out[outPos++] = out[ref++];
                while(outPos < len) {
                    out[outPos++] = out[ref++];
                }
            }
        } while(outPos < outLength);
    }

    @Deprecated
    public static void decompress2(final byte[] in, final int inPos_, final byte[] out, final int outPos_, final int outLength) {
        int inPos = inPos_;
        int outPos = outPos_;
        do {
            final int ctrl = in[inPos++] & 255;
            if(ctrl < MAX_LITERAL) {
                // literal run
                final int rounds = ctrl + 1;
                for(int i = 0; i < rounds; i++) {
                    out[outPos + i] = in[inPos + i];
                }
                outPos += rounds;
                inPos += rounds;
            } else {
                // back reference
                int len = ctrl >> 5;
                int ref = outPos - ((ctrl & 0x1f) << 8) - 1;
                if(len == 7) {
                    len += in[inPos++] & 255;
                }
                ref -= in[inPos++] & 255;
                out[outPos] = out[ref];
                out[outPos + 1] = out[ref + 1];
                final int rounds = len + 2;
                for(int i = 2; i < rounds; i++) {
                    out[outPos + i] = out[ref + i];
                }
                outPos += rounds;
                ref += rounds;
            }
        } while(outPos < outLength);
    }

    @Deprecated
    public static void decompressVectorized(final byte[] in, int inPos, final byte[] out, int outPos, final int outLength) {
        do {
            final int ctrl = in[inPos++] & 255;
            if(ctrl < MAX_LITERAL) {
                // literal run
                int i = 0;
                final int limit = ctrl - 7;
                while(i < limit) {
                    out[outPos] = in[inPos];
                    out[outPos + 1] = in[inPos + 1];
                    out[outPos + 2] = in[inPos + 2];
                    out[outPos + 3] = in[inPos + 3];
                    out[outPos + 4] = in[inPos + 4];
                    out[outPos + 5] = in[inPos + 5];
                    out[outPos + 6] = in[inPos + 6];
                    out[outPos + 7] = in[inPos + 7];
                    outPos += 8;
                    inPos += 8;
                    i += 8;
                }
                for(; i <= ctrl; i++) {
                    out[outPos++] = in[inPos++];
                }
            } else {
                // back reference
                int len = ctrl >> 5;
                int ref = outPos - ((ctrl & 0x1f) << 8) - 1;
                if(len == 7) {
                    len += in[inPos++] & 255;
                }
                ref -= in[inPos++] & 255;
                len += outPos + 2;
                out[outPos++] = out[ref++];
                out[outPos++] = out[ref++];
                final int rounds = len - outPos;
                final int limit = rounds - 7;
                int j = 0;
                while(j < limit) {
                    out[outPos] = out[ref];
                    out[outPos + 1] = out[ref + 1];
                    out[outPos + 2] = out[ref + 2];
                    out[outPos + 3] = out[ref + 3];
                    out[outPos + 4] = out[ref + 4];
                    out[outPos + 5] = out[ref + 5];
                    out[outPos + 6] = out[ref + 6];
                    out[outPos + 7] = out[ref + 7];
                    outPos += 8;
                    ref += 8;
                    j += 8;
                }
                for(; j < rounds; j++) {
                    out[outPos++] = out[ref++];
                }
            }
        } while(outPos < outLength);
    }

    private static int first(final byte[] in, final int inPos) {
        return (in[inPos] << 8) + (in[inPos + 1] & 255);
    }

    private static int next(final int v, final byte[] in, final int inPos) {
        return (v << 8) + (in[inPos + 2] & 255);
    }

    private static int hash(final int h) {
        return ((h * 184117) >> 9) & (HASH_SIZE - 1);
    }

}