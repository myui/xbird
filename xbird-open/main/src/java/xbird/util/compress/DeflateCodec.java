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

import static java.util.zip.Deflater.DEFAULT_COMPRESSION;

import java.util.zip.*;

import xbird.util.primitive.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DeflateCodec implements CompressionCodec {

    private final Deflater compressor;
    private final Inflater decompressor;

    public DeflateCodec() {
        this.compressor = new Deflater(DEFAULT_COMPRESSION, true);
        this.decompressor = new Inflater(true);
    }

    public byte[] compress(byte[] input) {
        return compress(input, DEFAULT_COMPRESSION);
    }

    public byte[] compress(final byte[] input, final int level) {
        // Create an expandable byte array to hold the compressed data.
        byte[] compressedData = new byte[input.length];

        compressor.reset();
        if(level != DEFAULT_COMPRESSION) {
            compressor.setLevel(level);
        }
        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();
        int compressedSize = compressor.deflate(compressedData);
        final int header;
        if(compressedSize == 0) {
            compressedData = input;
            compressedSize = input.length;
            header = 0;
        } else if(compressedSize >= (input.length - 4)) {
            compressedData = input;
            compressedSize = input.length;
            header = 0;
        } else {
            header = input.length;
        }
        final byte[] output = new byte[compressedSize + 4];
        output[0] = (byte) (header >> 24);
        output[1] = (byte) (header >> 16);
        output[2] = (byte) (header >> 8);
        output[3] = (byte) header;
        System.arraycopy(compressedData, 0, output, 4, compressedSize);
        return output;
    }

    public byte[] decompress(final byte[] in) {
        final int originalSize = (((in[0] & 0xff) << 24) + ((in[1] & 0xff) << 16)
                + ((in[2] & 0xff) << 8) + ((in[3] & 0xff)));
        if(originalSize == 0) {
            byte[] dest = new byte[in.length - 4];
            System.arraycopy(in, 4, dest, 0, in.length - 4);
            return dest;
        }
        // Create an expandable byte array to hold the decompressed data
        final byte[] result = new byte[originalSize];
        // Decompress the data
        decompressor.reset();
        decompressor.setInput(in, 4, in.length - 4);
        try {
            decompressor.inflate(result);
        } catch (DataFormatException e) {
            throw new IllegalStateException(e);
        }
        //decompressor.end();
        return result;
    }

    public char[] decompressAsChars(final byte[] in) {
        final int originalSize = (((in[0] & 0xff) << 24) + ((in[1] & 0xff) << 16)
                + ((in[2] & 0xff) << 8) + ((in[3] & 0xff)));
        if(originalSize == 0) {
            final int destlen = in.length - 4;
            byte[] dest = new byte[destlen];
            System.arraycopy(in, 4, dest, 0, destlen);
            return Primitives.toChars(dest, 0, destlen);
        }
        // Create an expandable byte array to hold the decompressed data
        final byte[] result = new byte[originalSize];
        // Decompress the data
        decompressor.reset();
        decompressor.setInput(in, 4, in.length - 4);
        try {
            decompressor.inflate(result);
        } catch (DataFormatException e) {
            throw new IllegalStateException(e);
        }
        return Primitives.toChars(result, 0, originalSize);
    }

}
