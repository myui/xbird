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
 *     Makoto YUI - ported from Apache commons codec (http://commons.apache.org/codec/).
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbird.util.codec;

import xbird.util.string.StringUtils;

/**
 * Provides Base64 encoding and decoding as defined by RFC 2045.
 * 
 * <p>This class implements section <cite>6.8. Base64 Content-Transfer-Encoding</cite> 
 * from RFC 2045 <cite>Multipurpose Internet Mail Extensions (MIME) Part One: 
 * Format of Internet Message Bodies</cite> by Freed and Borenstein.</p> 
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @author Apache Software Foundation
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @version $Id: Base64.java,v 1.20 2004/05/24 00:21:24 ggregory Exp $
 * @link http://commons.apache.org/codec/
 */
public final class Base64Codec {

    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;

    /**
     * Used to determine how many bits data contains.
     */
    private static final int TWENTYFOURBITGROUP = 24;

    /**
     * Used to get the number of Quadruples.
     */
    private static final int FOURBYTE = 4;

    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 64;

    /**
     * Used to test the sign of a byte.
     */
    private static final int SIGN = -128;

    /**
     * Byte used to pad output.
     */
    private static final byte PAD = (byte) '=';
    private static final char PAD_CHAR = '=';

    private static final char[] BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    // Create arrays to hold the base64 characters and a 
    // lookup for base64 chars
    private static final byte[] base64Alphabet = new byte[BASELENGTH];
    private static final byte[] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];

    // Populating the lookup and character arrays
    static {
        for(int i = 0; i < BASELENGTH; i++) {
            base64Alphabet[i] = (byte) -1;
        }
        for(int i = 'Z'; i >= 'A'; i--) {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for(int i = 'z'; i >= 'a'; i--) {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }
        for(int i = '9'; i >= '0'; i--) {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }
        base64Alphabet['+'] = 62;
        base64Alphabet['/'] = 63;

        for(int i = 0; i <= 25; i++) {
            lookUpBase64Alphabet[i] = (byte) ('A' + i);
        }
        for(int i = 26, j = 0; i <= 51; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('a' + j);
        }
        for(int i = 52, j = 0; i <= 61; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('0' + j);
        }
        lookUpBase64Alphabet[62] = (byte) '+';
        lookUpBase64Alphabet[63] = (byte) '/';
    }

    private Base64Codec() {}

    public static byte[] encode(final byte[] binaryData) {
        int lengthDataBits = binaryData.length * EIGHTBIT;
        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;

        final int encodedDataLength;
        if(fewerThan24bits != 0) { //data not divisible by 24 bit
            encodedDataLength = (numberTriplets + 1) * 4;
        } else { // 16 or 8 bit
            encodedDataLength = numberTriplets * 4;
        }

        final byte[] encodedData = new byte[encodedDataLength];

        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;

        for(i = 0; i < numberTriplets; i++) {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[(l << 2) | val3];
            encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];

            encodedIndex += 4;
        }

        // form integral number of 6-bit groups
        dataIndex = i * 3;

        if(fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) (b1 & 0x03);
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 2] = PAD;
            encodedData[encodedIndex + 3] = PAD;
        } else if(fewerThan24bits == SIXTEENBIT) {

            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = PAD;
        }

        return encodedData;
    }

    public static String toBase64String(final byte[] binaryData) {
        int lengthDataBits = binaryData.length * EIGHTBIT;
        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;

        final int encodedDataLength;
        if(fewerThan24bits != 0) { //data not divisible by 24 bit
            encodedDataLength = (numberTriplets + 1) * 4;
        } else { // 16 or 8 bit
            encodedDataLength = numberTriplets * 4;
        }

        final char[] encodedData = new char[encodedDataLength];

        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;

        for(i = 0; i < numberTriplets; i++) {
            dataIndex = i * 3;
            byte b1 = binaryData[dataIndex];
            byte b2 = binaryData[dataIndex + 1];
            byte b3 = binaryData[dataIndex + 2];

            byte l = (byte) (b2 & 0x0f);
            byte k = (byte) (b1 & 0x03);

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex] = BASE64CHARS[val1];
            encodedData[encodedIndex + 1] = BASE64CHARS[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = BASE64CHARS[(l << 2) | val3];
            encodedData[encodedIndex + 3] = BASE64CHARS[b3 & 0x3f];

            encodedIndex += 4;
        }

        // form integral number of 6-bit groups
        dataIndex = i * 3;

        if(fewerThan24bits == EIGHTBIT) {
            byte b1 = binaryData[dataIndex];
            byte k = (byte) (b1 & 0x03);
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex] = BASE64CHARS[val1];
            encodedData[encodedIndex + 1] = BASE64CHARS[k << 4];
            encodedData[encodedIndex + 2] = PAD_CHAR;
            encodedData[encodedIndex + 3] = PAD_CHAR;
        } else if(fewerThan24bits == SIXTEENBIT) {
            byte b1 = binaryData[dataIndex];
            byte b2 = binaryData[dataIndex + 1];
            byte l = (byte) (b2 & 0x0f);
            byte k = (byte) (b1 & 0x03);
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            encodedData[encodedIndex] = BASE64CHARS[val1];
            encodedData[encodedIndex + 1] = BASE64CHARS[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = BASE64CHARS[l << 2];
            encodedData[encodedIndex + 3] = PAD_CHAR;
        }

        return new String(encodedData);
    }

    public static byte[] decode(final String base64Data) {
        return decode(StringUtils.getBytes(base64Data));
    }

    public static byte[] decode(final byte[] base64Data) {
        return decode(base64Data, true);
    }

    public static byte[] decode(final byte[] base64Data, final boolean discardNonBase64Chars) {
        // RFC 2045 requires that we discard ALL non-Base64 characters
        final byte[] data = discardNonBase64Chars ? discardNonBase64(base64Data) : base64Data;
        // handle the edge case, so we don't have to worry about it later
        if(data.length == 0) {
            return new byte[0];
        }

        final int numberQuadruple = data.length / FOURBYTE;

        // Throw away anything not in base64Data
        final byte decodedData[];
        {
            // this sizes the output array properly - rlw
            int lastData = data.length;
            // ignore the '=' padding
            while(data[lastData - 1] == PAD) {
                if(--lastData == 0) {
                    return new byte[0];
                }
            }
            decodedData = new byte[lastData - numberQuadruple];
        }

        int dataIndex = 0, encodedIndex = 0;
        for(int i = 0; i < numberQuadruple; i++) {
            dataIndex = i * 4;
            final byte marker0 = data[dataIndex + 2];
            final byte marker1 = data[dataIndex + 3];

            final byte b1 = base64Alphabet[data[dataIndex]];
            final byte b2 = base64Alphabet[data[dataIndex + 1]];

            if(marker0 != PAD && marker1 != PAD) {//No PAD e.g 3cQl
                byte b3 = base64Alphabet[marker0];
                byte b4 = base64Alphabet[marker1];
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
            } else if(marker0 == PAD) {//Two PAD e.g. 3c[Pad][Pad]
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            } else if(marker1 == PAD) {//One PAD e.g. 3cQ[Pad]
                byte b3 = base64Alphabet[marker0];
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }

    /**
     * Discards any characters outside of the base64 alphabet, per
     * the requirements on page 25 of RFC 2045 - "Any characters
     * outside of the base64 alphabet are to be ignored in base64
     * encoded data."
     *
     * @param data The base-64 encoded data to groom
     * @return The data, less non-base64 characters (see RFC 2045).
     */
    private static byte[] discardNonBase64(final byte[] data) {
        byte groomedData[] = new byte[data.length];
        int bytesCopied = 0;

        for(int i = 0; i < data.length; i++) {
            if(isBase64(data[i])) {
                groomedData[bytesCopied++] = data[i];
            }
        }

        byte packedData[] = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
        return packedData;
    }

    private static boolean isBase64(final byte octect) {
        if(octect == PAD) {
            return true;
        } else if(base64Alphabet[octect] == -1) {
            return false;
        } else {
            return true;
        }
    }
}
