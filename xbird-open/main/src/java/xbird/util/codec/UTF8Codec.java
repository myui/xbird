/*
 * @(#)$Id: UTF8Converter.java 1001 2006-10-03 09:47:24Z yui $
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
package xbird.util.codec;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStream;

import xbird.util.collections.ints.IntArrayList;
import xbird.util.io.FastByteArrayOutputStream;

/**
 * This is a utility class encode/decode UTF-8.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public final class UTF8Codec {

    private static final char EOF = 0xFF; // non valid UTF8 String.

    private static final String ERR_MSG_INVALID_UTF8_ENCODING = "Invalid UTF-8 encoding";

    /** table for hexize. */
    private static final char[] hexSymbol = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    private UTF8Codec() {}

    public static byte[] encode(final int[] cp, final int length) {
        // determine how many bytes are needed for the complete conversion
        final int bytesNeeded = requiredBytes(cp);
        // allocate a byte[] of the necessary size
        final byte[] utf8 = new byte[bytesNeeded];
        // do the conversion from character code points to utf-8
        for(int i = 0, bytes = 0; i < length; i++) {
            if(cp[i] < 0x80) {
                utf8[bytes++] = (byte) cp[i];
            } else if(cp[i] < 0x0800) {
                utf8[bytes++] = (byte) (cp[i] >> 6 | 0xC0);
                utf8[bytes++] = (byte) (cp[i] & 0x3F | 0x80);
            } else if(cp[i] < 0x10000) {
                utf8[bytes++] = (byte) (cp[i] >> 12 | 0xE0);
                utf8[bytes++] = (byte) (cp[i] >> 6 & 0x3F | 0x80);
                utf8[bytes++] = (byte) (cp[i] & 0x3F | 0x80);
            } else if(cp[i] < 0x200000) {
                utf8[bytes++] = (byte) (cp[i] >> 18 | 0xF0);
                utf8[bytes++] = (byte) (cp[i] >> 12 & 0x3F | 0x80);
                utf8[bytes++] = (byte) (cp[i] >> 6 & 0x3F | 0x80);
                utf8[bytes++] = (byte) (cp[i] & 0x3F | 0x80);
            } else if(cp[i] < 0x4000000) {
                utf8[bytes++] = (byte) (cp[i] >> 24 | 0xF8);
                utf8[bytes++] = (byte) ((cp[i] >> 18 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] >> 12 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] >> 6 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] & 0x3F) | 0x80);
            } else { // already verified
                utf8[bytes++] = (byte) (cp[i] >> 30 | 0xFC);
                utf8[bytes++] = (byte) ((cp[i] >> 24 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] >> 18 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] >> 12 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] >> 6 & 0x3F) | 0x80);
                utf8[bytes++] = (byte) ((cp[i] & 0x3F) | 0x80);
            }
        }
        return utf8;
    }

    /**
     * Converts an array of Unicode scalar values (code points) into
     * UTF-8. This algorithm works under the assumption that all
     * surrogate pairs have already been converted into scalar code
     * point values within the argument.
     * 
     * get detail on this page.
     * http://developers.sun.com/dev/gadc/technicalpublications/articles/utf8.html
     * 
     * @param cp an array of Unicode scalar values (code points)
     * @return a byte[] containing the UTF-8 encoded characters
     */
    public static byte[] encode(final int[] cp) {
        return encode(cp, cp.length);
    }

    public static int[] decode(final byte[] binary, final int len) {
        if(len == 0) {
            return null;
        }
        IntArrayList res = new IntArrayList((int) (len * 0.75));
        int code;
        for(int offset = 0; (code = decodeCharacter(binary, offset, 0, 0)) != EOF; offset += characterSize(code)) {
            res.add(code);
        }
        return res.toArray();
    }

    public static int[] decode(final byte[] binary) {
        return decode(binary, binary.length);
    }

    public static int characterAt(final byte[] binary, final int index) {
        if(index < 1) {
            throw new IllegalArgumentException("Index MUST be more than 0, but was " + index);
        }
        int code = -1;
        int i = 0, offset = 0;
        while(i++ < index && (code = decodeCharacter(binary, offset, 0, 0)) != EOF) {
            offset += characterSize(code);
        }
        return code;
    }

    public static int requiredBytes(final int[] cp) {
        int bytesNeeded = 0;
        final int length = cp.length;
        for(int i = 0; i < length; i++) {
            bytesNeeded += characterSize(cp[i]);
        }
        return bytesNeeded;
    }

    public static int characterSize(final int in) {
        if(in < 0) { // sanity check
            // minus value (11111111: -1, 10000000: -128)
            throw new IllegalStateException(ERR_MSG_INVALID_UTF8_ENCODING);
        }
        if(in < 0x80) {
            // in < 128 (1byte) 
            return 1;
        } else if(in < 0x0800) {
            // 128 <= in < 2048 (2bytes).
            return 2;
        } else if(in < 0x10000) {
            // 2048 <= in < 65536 (3bytes)
            return 3;
        } else if(in < 0x200000) {
            // 65536 <= in < 2097152 (4bytes) 
            return 4;
        } else if(in < 0x4000000) {
            // 2097152 <= in < 67108864 (5bytes)
            return 5;
        } else if(in <= 0x7FFFFFFF) {
            // 67108864 <= in < 2147483648 (6bytes)
            return 6;
        } else { // sanity check
            throw new IllegalStateException(ERR_MSG_INVALID_UTF8_ENCODING);
        }
    }

    private static int decodeCharacter(final byte[] binary, final int offset, int outCode, int moreBytes) {
        final int length = binary.length;
        for(int i = offset; i < length; i++) {
            final byte b = binary[i];
            // Decodes UTF-8.
            if(b >= 0 && moreBytes == 0) {
                // 0xxxxxxx
                return b;
            } else if(((b & 0xC0) == 0x80) && (moreBytes != 0)) {
                // 10xxxxxx (continuation byte)
                outCode = (outCode << 6) | (b & 0x3F); // Adds 6 bits to code.
                if(--moreBytes == 0) {
                    return outCode;
                } else if(moreBytes < 0) { // sanity check
                    throw new IllegalStateException(ERR_MSG_INVALID_UTF8_ENCODING);
                } else {
                    return decodeCharacter(binary, i + 1, outCode, moreBytes);
                }
            } else if(((b & 0xE0) == 0xC0) && (moreBytes == 0)) {
                // 110xxxxx
                outCode = b & 0x1F;
                return decodeCharacter(binary, i + 1, outCode, 1);
            } else if(((b & 0xF0) == 0xE0) && (moreBytes == 0)) {
                // 1110xxxx
                outCode = b & 0x0F;
                return decodeCharacter(binary, i + 1, outCode, 2);
            } else if(((b & 0xF8) == 0xF0) && (moreBytes == 0)) {
                // 11110xxx
                outCode = b & 0x07;
                return decodeCharacter(binary, i + 1, outCode, 3);
            } else if(((b & 0xFC) == 0xF8) && (moreBytes == 0)) {
                // 111110xx
                outCode = b & 0x03;
                return decodeCharacter(binary, i + 1, outCode, 4);
            } else if(((b & 0xFE) == 0xFC) && (moreBytes == 0)) {
                // 1111110x
                outCode = b & 0x01;
                return decodeCharacter(binary, i + 1, outCode, 5);
            } else { // sanity check
                throw new IllegalStateException(ERR_MSG_INVALID_UTF8_ENCODING);
            }
        }
        return EOF;
    }

    /**
     * Converts bytea to Hex chars.
     */
    public static char[] toHex(final byte[] b) {
        final int length = b.length;
        final char[] ret = new char[length * 2];
        for(int i = 0; i < length; i++) {
            final int high = ((b[i] & 0xF0) >> 4);
            ret[i] = hexSymbol[high];
            final int low = (b[i] & 0x0F);
            ret[i + 1] = hexSymbol[low];
        }
        return ret;
    }

    public static int toHex(final char b) {
        final int high = ((b & 0xF0) >> 4);
        final int low = (b & 0x0F);
        int ret = hexSymbol[high] << 4;
        ret = (ret & hexSymbol[low]);
        return ret;
    }

    public static void writeUTF8(char c, OutputStream out) throws IOException {
        writeUTF8((int) c, out);
    }

    public static void writeUTF8(char c, FastByteArrayOutputStream out) {
        writeUTF8((int) c, out);
    }

    public static void writeUTF8(int code, OutputStream out) throws IOException {
        if((code & 0xffffff80) == 0) {
            out.write(code);
        } else if((code & 0xfffff800) == 0) { // 2 bytes.
            out.write(0xc0 | (code >> 6));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xffff0000) == 0) { // 3 bytes.
            out.write(0xe0 | (code >> 12));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xff200000) == 0) { // 4 bytes.
            out.write(0xf0 | (code >> 18));
            out.write(0x80 | ((code >> 12) & 0x3f));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xf4000000) == 0) { // 5 bytes.
            out.write(0xf8 | (code >> 24));
            out.write(0x80 | ((code >> 18) & 0x3f));
            out.write(0x80 | ((code >> 12) & 0x3f));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0x80000000) == 0) { // 6 bytes.
            out.write(0xfc | (code >> 30));
            out.write(0x80 | ((code >> 24) & 0x3f));
            out.write(0x80 | ((code >> 18) & 0x3f));
            out.write(0x80 | ((code >> 12) & 0x3F));
            out.write(0x80 | ((code >> 6) & 0x3F));
            out.write(0x80 | (code & 0x3F));
        } else {
            throw new CharConversionException("Illegal character: " + Integer.toHexString(code));
        }
    }

    public static void writeUTF8(int code, FastByteArrayOutputStream out) {
        if((code & 0xffffff80) == 0) {
            out.write(code);
        } else if((code & 0xfffff800) == 0) { // 2 bytes.
            out.write(0xc0 | (code >> 6));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xffff0000) == 0) { // 3 bytes.
            out.write(0xe0 | (code >> 12));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xff200000) == 0) { // 4 bytes.
            out.write(0xf0 | (code >> 18));
            out.write(0x80 | ((code >> 12) & 0x3f));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0xf4000000) == 0) { // 5 bytes.
            out.write(0xf8 | (code >> 24));
            out.write(0x80 | ((code >> 18) & 0x3f));
            out.write(0x80 | ((code >> 12) & 0x3f));
            out.write(0x80 | ((code >> 6) & 0x3f));
            out.write(0x80 | (code & 0x3f));
        } else if((code & 0x80000000) == 0) { // 6 bytes.
            out.write(0xfc | (code >> 30));
            out.write(0x80 | ((code >> 24) & 0x3f));
            out.write(0x80 | ((code >> 18) & 0x3f));
            out.write(0x80 | ((code >> 12) & 0x3F));
            out.write(0x80 | ((code >> 6) & 0x3F));
            out.write(0x80 | (code & 0x3F));
        } else {
            throw new IllegalArgumentException("Illegal character: " + Integer.toHexString(code));
        }
    }

}