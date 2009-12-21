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
package xbird.util.io;

import java.io.IOException;
import java.io.OutputStream;

import xbird.util.primitive.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BitOutputStream extends OutputStream {

    private static final byte[] masks = new byte[8];
    static {
        for(byte i = 0; i < 8; i++) {
            masks[i] = (byte) (~(0xFF >>> i));
        }
    }

    private final OutputStream out;

    private int pendingByte = 0;
    private int pendingNumberOfBits = 0;

    public BitOutputStream(OutputStream delegate) {
        this.out = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        write(Primitives.toBytes(b));
    }
    
    public void write(int b, int nbits) throws IOException {
        if(nbits < 0 || nbits > 32) {
            throw new IOException("Cannot write " + nbits + " bits from an int");
        }
        if(nbits < 8) {
            writeBits(b, nbits);
        } else {
            for(int i = 0;; i += 8) {
                final int v = b >>> (24 - i);
                if(i < nbits) {
                    writeBits(v, i % 8);
                    break;
                } else {
                    writeBits(v, 8);
                }
            }
        }
    }

    public void writeByte(int b) throws IOException {
        if(pendingNumberOfBits == 0) {
            emitByte(b);
        } else {
            pendingByte = (b & 0xFF >>> pendingNumberOfBits) | pendingByte;
            emitByte(pendingByte);
            pendingByte = (b << (8 - pendingNumberOfBits)) & 0xFF;
        }
    }    

    public void writeBits(int b, int nbits) throws IOException {
        if(nbits < 0 || nbits > 8) {
            throw new IOException("Cannot write " + nbits + " bits from a byte");
        }
        if(nbits == 8) {
            writeByte(b);
        } else {
            if(pendingNumberOfBits == 0) {
                pendingByte = (b << (8 - nbits)) & 0xFF;
                pendingNumberOfBits = nbits;
            } else {
                b = (byte) (b & ~masks[8 - nbits]);
                int pending = 8 - pendingNumberOfBits - nbits;
                if(pending < 0) {
                    pending = -pending;
                    pendingByte |= (b >>> pending);
                    emitByte(pendingByte);
                    pendingByte = (b << (8 - pending)) & 0xFF;
                    pendingNumberOfBits = pending;
                } else if(pending == 0) {
                    pendingByte = pendingByte | b;
                    emitByte(pendingByte);
                    pendingNumberOfBits = 0;
                } else {
                    pendingByte = pendingByte | (b << pending);
                    pendingNumberOfBits = 8 - pending;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        flush(true);
        out.close();
    }

    /**
     * Not flushed if there is a pending bytes. 
     */
    @Override
    public void flush() throws IOException {
        flush(false);
    }

    public void flush(boolean force) throws IOException {
        if(force && pendingNumberOfBits > 0) {
            emitByte(pendingByte);
        }
        out.flush();
    }

    private void emitByte(int b) throws IOException {
        out.write(b); // highest 24 bit is ignored
    }

}
