/*
 * @(#)$Id: FixedSegments.java 3619 2008-03-26 07:23:03Z yui $
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
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Fixed-size pager.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FixedSegments implements Segments {

    private final File file;
    private final int recordLength;
    private RandomAccessFile raf;
    private boolean open = false;
    private boolean dirty = true;

    public FixedSegments(final File file, final int recordLength) {
        this.file = file;
        this.recordLength = recordLength;
        try {
            ensureOpen();
        } catch (IOException e) {
            throw new IllegalStateException("loading file failed: " + file.getAbsolutePath(), e);
        }
    }

    private synchronized void ensureOpen() throws IOException {
        if(open) {
            return;
        }
        if(raf == null) {
            this.raf = new RandomAccessFile(file, "rw");
        }
        this.open = true;
    }

    public File getFile() {
        return file;
    }

    public long write(final long idx, final byte[] b) throws IOException {
        checkRecordLength(b);
        final long ptr = toPhysicalAddr(idx);
        ensureOpen();
        synchronized(raf) {// write to disk
            raf.seek(ptr);
            raf.write(b);
            this.dirty = true;
        }
        return ptr;
    }

    public long bulkWrite(final long idx, final byte[] b) throws IOException {
        final long ptr = toPhysicalAddr(idx);
        ensureOpen();
        synchronized(raf) {// write to disk
            raf.seek(ptr);
            raf.write(b);
            this.dirty = true;
        }
        return ptr;
    }

    private final long toPhysicalAddr(final long logicalAddr) {
        return logicalAddr * recordLength;
    }

    private final void checkRecordLength(final byte[] b) {
        if(b.length != recordLength) {
            throw new IllegalArgumentException("Invalid Record length: " + b.length
                    + ", expected: " + recordLength);
        }
    }

    public byte[] read(final long idx) throws IOException {
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

    public byte[] bulkRead(final long idx, final int numBlocks) throws IOException {
        final long ptr = toPhysicalAddr(idx);
        return directRead(ptr, numBlocks);
    }

    public final void bulkRead(final long idx, final byte[] b) throws IOException {
        ensureOpen();
        final long addr = toPhysicalAddr(idx);
        synchronized(raf) {// read from disk
            raf.seek(addr);
            raf.read(b);
        }
    }

    private final byte[] directRead(final long addr, final int numBlocks) throws IOException {
        ensureOpen();
        final byte[] b = new byte[recordLength * numBlocks];
        synchronized(raf) {// read from disk
            raf.seek(addr);
            raf.read(b);
        }
        return b;
    }

    public void flush(final boolean close) throws IOException {
        if(!dirty) {
            return;
        }
        if(!open) {
            throw new IllegalStateException();
        }
        raf.getChannel().force(true);
        this.dirty = false;
        if(close) {
            close();
        }
    }

    public void close() throws IOException {
        if(raf != null) {
            raf.close();
            this.raf = null;
        }
        this.open = false;
    }

}
