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

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CompressorFactory {

    private static final String codec = Settings.get("xbird.database.compression", null);

    private CompressorFactory() {}

    public static CompressionCodec createCodec() {
        if("deflate".equals(codec)) {
            return new DeflateCodec();
        } else if("lzf".equals(codec)) {
            return new LZFCodec();
        } else if("lzss".equals(codec)) {
            return new LZSSCodec();
        }
        return new NoCompressionCodec();
    }

    public static CompressionCodec createCodec(CompressionCodecType type) {
        switch(type) {
            case deflate:
                return new DeflateCodec();
            case lzf:
                return new LZFCodec();
            case lzss:
                return new LZSSCodec();
            case nop:
                return new NoCompressionCodec();
            default:
                throw new IllegalStateException("unexpected type: " + type);
        }
    }

    public enum CompressionCodecType {
        nop, deflate, lzf, lzss;
    }
}
