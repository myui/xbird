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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import xbird.util.primitive.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CDB {
    private static final int HASHSTART = 5381;
    private static final int NUM_SLOTS = 1024;
    private static final int HEADER_SIZE = NUM_SLOTS * (8 + 8);

    private final int[] slotPos;
    private final int[] slotLen;

    /** CDB File */
    private RandomAccessFile raf;
    private long datapos = HEADER_SIZE;

    public CDB(File file) {
        try {
            this.raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
        this.slotPos = new int[NUM_SLOTS];
        this.slotLen = new int[NUM_SLOTS];
        if(file.exists()) {
            try {
                readHeader();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void readHeader() throws IOException {
        final byte[] b = new byte[HEADER_SIZE];
        raf.readFully(b);

        int offset = 0;
        for(int i = 0; i < NUM_SLOTS; i++) {
            slotPos[i] = Primitives.getInt(b, offset);
            slotLen[i] = Primitives.getInt(b, offset + 4);
            offset += 8;
        }
    }
    
    public void add(byte[] key, byte[] data) throws IOException {
        
    }

}