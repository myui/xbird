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
import java.io.IOException;

import xbird.storage.DbException;
import xbird.storage.index.BIndexFile;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class VarSegmentsBFile implements Segments {

    private final BIndexFile bfile;

    public VarSegmentsBFile(File file) {
        final BIndexFile bf = new BIndexFile(file, false);
        try {
            bf.init(false);
        } catch (DbException e) {
            throw new IllegalStateException("failed on initializing BFile: "
                    + file.getAbsolutePath(), e);
        }
        this.bfile = bf;
    }

    public File getFile() {
        return bfile.getFile();
    }

    public byte[] read(long idx) throws IOException {
        try {
            return bfile.getValueBytes(idx); //FIXME synchronized
        } catch (DbException e) {
            throw new IOException("read failed on idx#" + idx);
        }
    }

    public byte[][] readv(long[] idx) throws IOException {
        final int len = idx.length;
        final byte[][] pages = new byte[len][];
        for(int i = 0; i < len; i++) {
            pages[i] = read(idx[i]);
        }
        return pages;
    }

    public long write(long idx, byte[] b) throws IOException {
        try {
            return bfile.addValue(idx, b);
        } catch (DbException e) {
            throw new IOException("write failed on idx#" + idx);
        }
    }

    public void close() throws IOException {
        try {
            bfile.close();
        } catch (DbException e) {
            throw new IOException("close failed: " + bfile.getFile().getAbsolutePath());
        }
    }

    public void flush(boolean close) throws IOException {
        try {
            bfile.flush(true, true);
        } catch (DbException e) {
            throw new IOException("flush failed: " + bfile.getFile().getAbsolutePath());
        }
        if(close) {
            close();
        }
    }

}
