/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.resource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import xbird.util.io.FastBufferedInputStream;
import xbird.util.io.FastBufferedOutputStream;
import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PropertyMap implements Serializable {
    private static final long serialVersionUID = 1268144009034765788L;

    private final File file;
    private final Map<String, String> _entries;
    private final List<String> _volatileEntries;

    private int shirinkThreshold = 4096;
    private transient int duplicateEnrties = 0; // TODO

    public PropertyMap(File file) {
        this(file, new ConcurrentHashMap<String, String>(128));
    }

    private PropertyMap(File file, ConcurrentMap<String, String> entries) {
        this.file = file;
        this._entries = entries;
        this._volatileEntries = new ArrayList<String>(64);
    }

    public void setShirinkThreshold(int shirinkThreshold) {
        this.shirinkThreshold = shirinkThreshold;
    }

    public File getFile() {
        return file;
    }

    public String getProperty(String key) {
        return _entries.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        final String value = _entries.get(key);
        if(value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public synchronized String setProperty(String key, String value) {
        final String oldValue = _entries.put(key, value);
        if(oldValue == null) {
            _volatileEntries.add(key);
            _volatileEntries.add(value);
        } else if(!oldValue.equals(value)) {
            _volatileEntries.add(key);
            _volatileEntries.add(value);
            duplicateEnrties++;
        }
        return oldValue;
    }

    public static PropertyMap load(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FastBufferedInputStream bis = new FastBufferedInputStream(fis, 4096);
        DataInputStream dis = new DataInputStream(bis);

        final int totalEntries = dis.readInt();
        final int duplicateEntries = dis.readInt();
        ConcurrentMap<String, String> entries = new ConcurrentHashMap<String, String>((totalEntries >>> 1) * 3); // totalSize * 3/2        
        try {
            while(true) {
                String key = IOUtils.readString(dis);
                String value = IOUtils.readString(dis);
                entries.put(key, value);
            }
        } catch (EOFException expectedEof) {
            dis.close();
        } finally {
            fis.close();
        }

        final PropertyMap map = new PropertyMap(file, entries);
        map.duplicateEnrties = duplicateEntries;
        return map;
    }

    public synchronized void save() throws IOException {
        final int totalEntries = _entries.size();

        final FileOutputStream fos;
        final boolean attemptShrink = duplicateEnrties > shirinkThreshold;
        if(attemptShrink) {
            fos = new FileOutputStream(file, false);
            this.duplicateEnrties = 0;
        } else {
            // reset headers
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.writeInt(totalEntries);
            raf.writeInt(duplicateEnrties);
            raf.getFD().sync();
            raf.close();

            fos = new FileOutputStream(file, true);
        }

        final FastBufferedOutputStream bos = new FastBufferedOutputStream(fos, 4096);
        final DataOutputStream out = new DataOutputStream(bos);

        if(attemptShrink) {
            out.writeInt(totalEntries);
            out.writeInt(0); // duplicateEnrties
            for(Entry<String, String> e : _entries.entrySet()) {
                IOUtils.writeString(out, e.getKey());
                IOUtils.writeString(out, e.getValue());
            }
        } else {//append
            final int volatileSize = _volatileEntries.size();
            for(int i = 0; i < volatileSize; i += 2) {
                String key = _volatileEntries.get(i);
                IOUtils.writeString(out, key);
                String value = _volatileEntries.get(i + 1);
                IOUtils.writeString(out, value);
            }
        }
        _volatileEntries.clear();

        out.flush();
        out.close();
    }

}
