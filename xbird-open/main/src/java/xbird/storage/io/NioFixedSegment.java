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
package xbird.storage.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import xbird.util.nio.NIOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NioFixedSegment implements Segments {

    private final File file;
    private final int recordLength;
    private final FileChannel channel;

    public NioFixedSegment(File file, int recordLength) {
        this(file, recordLength, false);
    }

    public NioFixedSegment(File file, int recordLength, boolean readOnly) {
        this.file = file;
        this.recordLength = recordLength;
        final RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, readOnly ? "r" : "rw");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File not found: " + file.getAbsolutePath(), e);
        }
        this.channel = raf.getChannel();
    }

    public File getFile() {
        return file;
    }

    public byte[] read(long idx) throws IOException {
        final long ptr = toPhysicalAddr(idx);
        return directRead(ptr, 1);
    }

    public byte[][] readv(long[] idx) throws IOException {
        final int len = idx.length;
        final byte[][] pages = new byte[len][];
        for(int i = 0; i < len; i++) {
            pages[i] = read(idx[i]);
        }
        return pages;
    }

    private byte[] directRead(final long addr, final int numBlocks) throws IOException {
        final byte[] b = new byte[recordLength * numBlocks];
        final ByteBuffer buf = ByteBuffer.wrap(b);
        NIOUtils.readFully(channel, buf, addr);
        return b;
    }

    public long write(long idx, byte[] b) throws IOException {
        checkRecordLength(b);
        final long ptr = toPhysicalAddr(idx);
        final ByteBuffer writeBuf = ByteBuffer.wrap(b);
        NIOUtils.writeFully(channel, writeBuf, ptr);
        return ptr;
    }

    public void close() throws IOException {
        channel.close();
    }

    public void flush(boolean close) throws IOException {
        channel.force(true);
    }

    private long toPhysicalAddr(final long logicalAddr) {
        return logicalAddr * recordLength;
    }

    private void checkRecordLength(final byte[] b) {
        if(b.length != recordLength) {
            throw new IllegalArgumentException("Invalid Record length: " + b.length
                    + ", expected: " + recordLength);
        }
    }
}
