/*
 * @(#)$Id: StringUtils.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.string;

import xbird.util.lang.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StringUtils {

    public static String toBitString(final double d) {
        final char[] bit = new char[64];
        final long dd = Double.doubleToLongBits(d);
        long mask = 1L;
        for(int i = 0; i < 64; i++) {
            final long bitval = dd & mask;
            if(bitval == 0) {
                bit[63 - i] = '0';
            } else {
                bit[63 - i] = '1';
            }
            mask <<= 1;
        }
        return String.valueOf(bit);
    }

    public static String toBitString(final float f) {
        final char[] bit = new char[32];
        final int ff = Float.floatToIntBits(f);
        int mask = 1;
        for(int i = 0; i < 32; i++) {
            final int bitval = ff & mask;
            if(bitval == 0) {
                bit[31 - i] = '0';
            } else {
                bit[31 - i] = '1';
            }
            mask <<= 1;
        }
        return String.valueOf(bit);
    }

    public static String toBitString(final long n) {
        final char[] bit = new char[64];
        long mask = 1L;
        for(int i = 0; i < 64; i++) {
            final long bitval = n & mask;
            if(bitval == 0) {
                bit[63 - i] = '0';
            } else {
                bit[63 - i] = '1';
            }
            mask <<= 1;
        }
        return String.valueOf(bit);
    }

    public static String toBitString(final int n) {
        final char[] bit = new char[32];
        int mask = 1;
        for(int i = 0; i < 32; i++) {
            final int bitval = n & mask;
            if(bitval == 0) {
                bit[31 - i] = '0';
            } else {
                bit[31 - i] = '1';
            }
            mask <<= 1;
        }
        return String.valueOf(bit);
    }

    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for(int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for(int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if(bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }

    public static String strip(String src, String stripChars) {
        return (String) strip((CharSequence) src, stripChars);
    }

    /**
     * @param stripChars if null, remove leading unicode whitespaces.
     */
    public static CharSequence strip(CharSequence src, String stripChars) {
        if(src == null || src.length() == 0) {
            return src;
        }
        final CharSequence striped = stripStart(src, stripChars);
        return stripEnd(striped, stripChars);
    }

    /**
     * @param stripChars if null, remove leading unicode whitespaces.
     */
    public static CharSequence stripStart(CharSequence src, String stripChars) {
        int srclen;
        if(src == null || (srclen = src.length()) == 0) {
            return src;
        }
        int start = 0;
        if(stripChars == null) {
            while((start != srclen) && Character.isWhitespace(src.charAt(start))) {
                start++;
            }
        } else if(stripChars.length() == 0) {
            return src;
        } else {
            while((start != srclen) && (stripChars.indexOf(src.charAt(start)) != -1)) {
                start++;
            }
        }
        return src.subSequence(start, srclen);
    }

    /**
     * @param stripChars if null, remove leading unicode whitespaces.
     */
    public static CharSequence stripEnd(CharSequence src, String stripChars) {
        int end;
        if(src == null || (end = src.length()) == 0) {
            return src;
        }
        if(stripChars == null) {
            while((end != 0) && Character.isWhitespace(src.charAt(end - 1))) {
                end--;
            }
        } else if(stripChars.length() == 0) {
            return src;
        } else {
            while((end != 0) && (stripChars.indexOf(src.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return src.subSequence(0, end);
    }

    /**
     * Checks whether the String a valid Java number.
     * this code is ported from jakarta commons lang.
     * 
     * @link http://jakarta.apache.org/commons/lang/apidocs/org/apache/commons/lang/math/NumberUtils.html
     */
    public static boolean isNumber(String str) {
        if(str == null || str.length() == 0) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if(sz > start + 1) {
            if(chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if(i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for(; i < chars.length; i++) {
                    if((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while(i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if(chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if(chars[i] == '.') {
                if(hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent   
                    return false;
                }
                hasDecPoint = true;
            } else if(chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if(hasExp) {
                    // two E's
                    return false;
                }
                if(!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if(chars[i] == '+' || chars[i] == '-') {
                if(!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if(i < chars.length) {
            if(chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if(chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if(!allowSigns
                    && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if(chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    public static boolean equals(final String trg, final char[] ch, final int offset, final int length) {
        assert (trg != null && ch != null);
        final int trglen = trg.length();
        if(trglen != length) {
            return false;
        }
        for(int i = offset; i < length; i++) {
            final char tc = trg.charAt(0);
            if(tc != ch[offset + 1]) {
                return false;
            }
        }
        return true;
    }

    public static char[] getChars(final String s) {
        final int len = s.length();
        final char[] dst = new char[len];
        s.getChars(0, len, dst, 0);
        return dst;
    }

    public static byte[] getBytes(final String s) {
        final int len = s.length();
        final byte[] b = new byte[len * 2];
        for(int i = 0; i < len; i++) {
            Primitives.putChar(b, i * 2, s.charAt(i));
        }
        return b;
    }

    public static byte[][] toBytes(final String[] args) {
        final int len = args.length;
        final byte[][] b = new byte[len][];
        for(int i = 0; i < len; i++) {
            b[i] = StringUtils.getBytes(args[i]);
        }
        return b;
    }

    public static String toString(byte[] b) {
        return toString(b, 0, b.length);
    }

    public static String toString(byte[] b, int off, int len) {
        final int clen = len >>> 1;
        final char[] c = new char[clen];
        for(int i = 0; i < clen; i++) {
            final int j = off + (i << 1);
            c[i] = (char) ((b[j + 1] & 0xFF) + ((b[j + 0]) << 8));
        }
        return new String(c);
    }

    private static final float KBYTES = 1024f;
    private static final float MBYTES = KBYTES * KBYTES;
    private static final float GBYTES = MBYTES * KBYTES;

    public static String displayBytesSize(final long n) {
        final String size;
        final long abs = Math.abs(n);
        if(abs < KBYTES) {
            size = n + " bytes";
        } else if(abs < MBYTES) {
            size = String.format("%.2f", n / KBYTES) + " kB";
        } else if(abs < GBYTES) {
            size = String.format("%.2f", n / MBYTES) + " MB";
        } else {
            return String.format("%.2f", n / GBYTES) + " GB";
        }
        return size;
    }

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final int UPPER_NIBBLE_MASK = 0xF0;
    private static final int LOWER_NIBBLE_MASK = 0x0F;

    public static void encodeHex(final byte b, final StringBuilder buf) {
        final int upper = (b & UPPER_NIBBLE_MASK) >> 4;
        final int lower = b & LOWER_NIBBLE_MASK;
        buf.append(HEX_DIGITS[upper]);
        buf.append(HEX_DIGITS[lower]);
    }

    public static String encodeHex(final byte buf[]) {
        final int buflen = buf.length;
        final char[] ch = new char[buflen * 2];
        for(int i = 0, j = 0; i < buf.length; i++, j += 2) {
            final byte b = buf[i];
            final int upper = (b & UPPER_NIBBLE_MASK) >> 4;
            final int lower = b & LOWER_NIBBLE_MASK;
            ch[j] = HEX_DIGITS[upper];
            ch[j + 1] = HEX_DIGITS[lower];
        }
        return new String(ch);
    }

}
