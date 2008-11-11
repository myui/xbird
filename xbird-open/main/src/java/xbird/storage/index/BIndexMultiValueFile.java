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
package xbird.storage.index;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbException;
import xbird.util.lang.ArrayUtils;
import xbird.util.lang.Primitives;
import xbird.util.lang.PrintUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BIndexMultiValueFile extends BIndexFile {
    private static final Log LOG = LogFactory.getLog(BIndexMultiValueFile.class);

    public BIndexMultiValueFile(File file) {
        super(file, false);
        BFileHeader fh = getFileHeader();
        fh.setMultiValue(true);
    }

    public BIndexMultiValueFile(File file, int pageSize, int caches) {
        super(file, pageSize, caches, false);
        BFileHeader fh = getFileHeader();
        fh.setMultiValue(true);
    }

    @Override
    public synchronized long putValue(Value key, Value value) throws DbException {
        final long valuePtr = storeValue(value);
        final long ptr = findValue(key);
        if(ptr != KEY_NOT_FOUND) {// key found
            // update the page
            byte[] ptrTuple = retrieveTuple(ptr);
            MultiPtrs ptrs = MultiPtrs.readFrom(ptrTuple);
            ptrs.addPointer(valuePtr);
            updateValue(ptrs, ptr);
            return ptr;
        } else {
            // insert a new key           
            long newPtr = storeValue(new MultiPtrs(valuePtr));
            addValue(key, newPtr);
            return newPtr;
        }
    }

    @Override
    protected BTreeCallback getHandler(BTreeCallback handler) {
        return new BIndexMultiValueCallback(handler);
    }

    private final class BIndexMultiValueCallback implements BTreeCallback {

        final BTreeCallback handler;

        public BIndexMultiValueCallback(BTreeCallback handler) {
            this.handler = handler;
        }

        public boolean indexInfo(Value key, long pointer) {
            final byte[] ptrTuple;
            try {
                ptrTuple = retrieveTuple(pointer);
            } catch (DbException e) {
                throw new IllegalStateException(e);
            }
            MultiPtrs ptrs = MultiPtrs.readFrom(ptrTuple);
            final long[] lptrs = ptrs.getPointers();
            for(int i = 0; i < lptrs.length; i++) {
                final long lptr = lptrs[i];
                final byte[] value;
                try {
                    value = retrieveTuple(lptr);
                } catch (DbException e) {
                    LOG.error(PrintUtils.prettyPrintStackTrace(e));
                    throw new IllegalStateException(e);
                }
                if(!handler.indexInfo(key, value)) {
                    return false;
                }
            }
            return true;
        }

        public boolean indexInfo(Value key, byte[] value) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class MultiPtrs extends Value {

        private long[] ptrs;

        public MultiPtrs() {
            super();
        }

        public MultiPtrs(long... ptrs) {
            super(toBytes(ptrs));
            this.ptrs = ptrs;
        }

        public long[] getPointers() {
            return ptrs;
        }

        public void addPointer(long ptr) {
            this.ptrs = ArrayUtils.insert(ptrs, ptr);
            byte[] b = toBytes(ptrs);
            this._data = b;
            this._pos = 0;
            this._len = b.length;
        }

        private static byte[] toBytes(long[] ptrs) {
            final int len = ptrs.length;
            final int size = 4 + (8 * len);
            final byte[] b = new byte[size];
            Primitives.putInt(b, 0, len);
            int idx = 4;
            for(int i = 0; i < len; i++) {
                long ptr = ptrs[i];
                Primitives.putLong(b, idx, ptr);
                idx += 8;
            }
            return b;
        }

        public static MultiPtrs readFrom(byte[] b) {
            final int len = Primitives.getInt(b, 0);
            final long[] ptrs = new long[len];
            int idx = 4;
            for(int i = 0; i < len; i++) {
                long ptr = Primitives.getLong(b, idx);
                ptrs[i] = ptr;
                idx += 8;
            }
            return new MultiPtrs(ptrs);
        }

    }
}
