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

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://d.hatena.ne.jp/odz/20060623/1151107174
 * @link http://www.corpit.ru/mjt/tinycdb.html
 */
public class ReadOnlyCDB implements Closeable {
    private static final int HASHSTART = 5381;

    /** map for CDB file */
    private ByteBuffer map;

    /** CDB File */
    private RandomAccessFile file;

    /** number of hash slots searched under this key */
    private int loop;

    /** initialized if loop is nonzero */
    private int khash;

    private int kpos;

    private int hpos;

    private int hslots;

    public ReadOnlyCDB(String filename) throws IOException {
        file = new RandomAccessFile(filename, "r");
        FileChannel channel = file.getChannel();
        map = channel.map(MapMode.READ_ONLY, 0, file.length());
        map.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void close() {
        try {
            file.close();
            file = null;
            map = null;
        } catch (IOException ex) {
        }
    }

    public void findstart() {
        loop = 0;
    }

    public byte[] findnext(byte[] key, int offset, int len) {
        int u;
        int dlen;

        if(loop == 0) {
            u = hash(key, offset, len);
            map.position((u << 3) & 2047);
            hpos = map.getInt();
            hslots = map.getInt();
            if(hslots == 0) {
                return null;
            }
            khash = u;
            u >>>= 8;
            u %= hslots;
            u <<= 3;
            kpos = hpos + u;
        }

        while(loop < hslots) {
            int pos;

            map.position(kpos);
            u = map.getInt();
            pos = map.getInt();
            if(pos == 0) {
                return null;
            }

            loop++;
            kpos += 8;

            if(kpos == hpos + (hslots << 3)) {
                kpos = hpos;
            }

            if(u == khash) {
                map.position(pos);
                u = map.getInt();
                if(u == len) {
                    dlen = map.getInt();
                    byte[] keyInDb = new byte[len];
                    map.get(keyInDb, 0, len);
                    if(!match(key, offset, len, keyInDb)) {
                        return null;
                    }

                    byte[] data = new byte[dlen];
                    map.get(data, 0, dlen);
                    return data;
                }
            }
        }

        return null;
    }

    public byte[] findnext(byte[] key) {
        return findnext(key, 0, key.length);
    }


    public byte[] find(byte[] key) {
        return find(key, 0, key.length);
    }
    
    public byte[] find(byte[] key, int offset, int len) {
        findstart();
        return findnext(key, offset, len);
    }

    private static int hash(byte[] buffer, int offset, int len) {
        long h = HASHSTART;
        final long mask = 0x00000000ffffffffL;
        for(int i = 0; i < len; i++) {
            h = (h + (h << 5) & mask) & mask;
            h = h ^ ((buffer[offset + i] + 0x100) & 0xff);
        }
        return (int) (h & mask);
    }

    private static boolean match(byte[] key, int offset, int len, byte[] keyInDb) {
        for(int i = 0; i < len; i++) {
            if(key[offset + i] != keyInDb[i]) {
                return false;
            }
        }
        return true;
    }
}